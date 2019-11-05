package com.pdftron.realtimecollaborationws

import android.util.Log
import com.pdftron.collab.db.entity.AnnotationEntity
import com.pdftron.collab.utils.XfdfUtils
import com.pdftron.fdf.FDFDoc
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class WSConnection : WebSocketListener() {

    private lateinit var mBroadcaster: FlowableEmitter<ServerEvent>
    private var mFlowableDisposable: Disposable? = null
    private val mFlowable = Flowable.create(
        FlowableOnSubscribe<ServerEvent> { emitter -> mBroadcaster = emitter },
        BackpressureStrategy.BUFFER
    )

    private val mDisposables = CompositeDisposable()

    internal var client: OkHttpClient? = null
    internal var mWebSocket: WebSocket? = null

    init {
        mFlowableDisposable = mFlowable.subscribe()

        client = OkHttpClient.Builder()
            .build()
        val request = Request.Builder().url(WS_URL).build()
        mWebSocket = client!!.newWebSocket(request, this)

        client!!.dispatcher().executorService().shutdown()
    }

    fun start(): Flowable<ServerEvent> {
        mDisposables.add(loadXfdfStrings().subscribeOn(Schedulers.io()).subscribe())
        return mFlowable
    }

    fun close() {
        mFlowableDisposable?.dispose()
        client = null
        if (mWebSocket != null) {
            mWebSocket!!.close(NORMAL_CLOSURE_STATUS, "Goodbye!")
        }
        mDisposables.clear()
    }

    fun sendMessage(annotations: ArrayList<AnnotationEntity>) {
        for (annot in annotations) {
            val json = JSONObject()
            json.put("annotationId", annot.id)
            json.put("documentId", DOCUMENT_ID)
            json.put("xfdfString", XfdfUtils.validateXfdf(annot.xfdf))
            val result = json.toString()
            if (mWebSocket != null) {
                mWebSocket!!.send(result)
            }
        }
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {

    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d(TAG, "onMessage: " + text!!)
        try {
            val body = JSONObject(text)
            val xfdfCommand = body.optString("xfdfString")
            mBroadcaster.onNext(ServerEvent.importXfdfCommand(xfdfCommand))

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {

    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {

    }

    private fun loadXfdfStrings(): Completable {
        return Completable.fromAction { loadXfdfStringsImpl() }
    }

    @Throws(IOException::class, JSONException::class)
    private fun loadXfdfStringsImpl() {
        val response = get(ANNOTATION_HANDLER_url)
        if (response != null && response.successful) {
            val rows = JSONArray(response.body)
            val fdfDoc = FDFDoc()

            for (i in 0 until rows.length()) {
                val row = rows.getJSONObject(i)
                val xfdfCommand = row.optString("xfdfString")
                fdfDoc.mergeAnnots(xfdfCommand)
            }

            var xfdfCommands = fdfDoc.saveAsXFDF()
            // turn into command
            xfdfCommands = xfdfCommands.replace("<annots>", "<add>")
            xfdfCommands = xfdfCommands.replace("</annots>", "</add>")

            mBroadcaster.onNext(ServerEvent.importXfdfCommand(xfdfCommands))
        }
    }

    @Throws(IOException::class)
    private operator fun get(url: String): ResponsePair? {
        val httpUrl = HttpUrl.parse(url)
        if (httpUrl != null) {
            val request = Request.Builder()
                .url(httpUrl)
                .build()

            client!!.newCall(request).execute().use { response ->
                if (response.body() != null) {
                    return ResponsePair(
                        response.code(),
                        response.isSuccessful,
                        response.body()!!.string()
                    )
                }
            }
        }
        return null
    }

    internal class ResponsePair(var code: Int, var successful: Boolean, var body: String)

    companion object {
        internal val TAG = WSConnection::class.java.simpleName

        internal const val LOCALHOST_IP = "192.168.10.31"

        internal const val WS_URL = "ws://$LOCALHOST_IP:8080/"

        internal const val DOCUMENT_ID = "webviewer-demo-1"

        internal const val ANNOTATION_HANDLER_url =
            "http://$LOCALHOST_IP:3000/server/annotationHandler.js?documentId=$DOCUMENT_ID"

        internal const val NORMAL_CLOSURE_STATUS = 1000
    }
}