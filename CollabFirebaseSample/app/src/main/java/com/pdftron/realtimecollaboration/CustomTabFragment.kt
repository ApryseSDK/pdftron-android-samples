package com.pdftron.realtimecollaboration

import com.pdftron.collab.ui.reply.bottomsheet.ReplyFragment
import com.pdftron.collab.ui.reply.bottomsheet.ReplyFragmentBuilder
import com.pdftron.collab.ui.viewer.CollabViewerTabFragment

class CustomTabFragment : CollabViewerTabFragment() {
    // Define the layout XML file to use for this viewer fragment
    override fun getContentLayout(): Int {
        return R.layout.fragment_custom_reply_viewer
    }
    // Instantiate and display the reply UI in the reply container
    override fun showReplyFragment(selectedAnnotId: String, authorId: String, selectedAnnotPageNum: Int) {
        // Create our reply fragment
        val fragment = ReplyFragmentBuilder
            .withAnnot(mDocumentId, selectedAnnotId, authorId)
            .usingTheme(mReplyTheme)
            .build(activity!!, ReplyFragment::class.java)
        // Add the fragment to the container
        activity!!.supportFragmentManager.beginTransaction()
            .add(R.id.reply_container, fragment)
            .commit()
    }
}