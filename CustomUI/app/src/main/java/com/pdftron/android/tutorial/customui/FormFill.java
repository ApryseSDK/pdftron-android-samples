//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.tutorial.customui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Action;
import com.pdftron.pdf.ActionParameter;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.ColorSpace;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.Font;
import com.pdftron.pdf.GState;
import com.pdftron.pdf.KeyStrokeActionResult;
import com.pdftron.pdf.KeyStrokeEventData;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.ViewChangeCollection;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.dialog.SimpleDateTimePickerFragment;
import com.pdftron.pdf.tools.DialogFormFillChoice;
import com.pdftron.pdf.tools.DialogFormFillText;
import com.pdftron.pdf.tools.RichMedia;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.tools.ToolManager.ToolMode;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.ShortcutHelper;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;
import com.pdftron.pdf.widget.AutoScrollEditText;
import com.pdftron.pdf.widget.AutoScrollEditor;
import com.pdftron.sdf.Obj;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class is responsible for filling forms.
 */
@Keep
public class FormFill extends Tool {

    private Field mField;
    private AutoScrollEditor mEditor;
    private boolean mIsMultiLine;
    private double mBorderWidth;
    private boolean mHasClosed = false;

    private boolean mCanUseDateTimePicker = true;

    private static final String PICKER_TAG = "simple_date_picker";

    private boolean mUseEditTextAppearance = true;

    /**
     * Class constructor
     */
    public FormFill(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
        mEditor = null;
        mBorderWidth = 0;
    }

    /**
     * The overload implementation of {@link Tool#getToolMode()}.
     */
    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return ToolMode.FORM_FILL;
    }

    @Override
    public int getCreateAnnotType() {
        return Annot.e_Unknown;
    }

    @Override
    public boolean onFlingStop() {
        if (mEditor != null) {
            mEditor.onCanvasSizeChanged();
        }
        return super.onFlingStop();
    }

    /**
     * Execute Action
     * <p>
     * <div class="warning">
     * The PDF doc should have been locked when call this method.
     * In addition, ToolManager's raise annotation should be handled in the caller function.
     * </div>
     */
    private void executeAction(Field fld, int type) {
        if (mAnnot != null) {
            try {
                Obj aa = mAnnot.getTriggerAction(type);
                if (aa != null) {
                    Action a;
                    a = new Action(aa);
                    ActionParameter action_param;
                    action_param = new ActionParameter(a, fld);
                    executeAction(action_param);
                }
            } catch (PDFNetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Execute Action
     * <p>
     * <div class="warning">
     * The PDF doc should have been locked when call this method.
     * In addition, ToolManager's raise annotation should be handled in the caller function.
     * </div>
     */
    private void executeAction(Annot annot, int type) {
        if (annot != null) {
            try {
                Obj aa = annot.getTriggerAction(type);
                if (aa != null) {
                    Action a;
                    a = new Action(aa);
                    ActionParameter action_param;
                    action_param = new ActionParameter(a, annot);
                    executeAction(action_param);

                    if (action_param.getAction().getType() == Action.e_Unknown) {
                        String mediaCmd = getMediaCmd(annot);
                        if (!Utils.isNullOrEmpty(mediaCmd)) {
                            // this is a multi-media cmd
                            if (mediaCmd.contains("rewind") || mediaCmd.contains("play")) {
                                // play media
                                Obj cmdTA = getLinkedMedia(annot);
                                if (cmdTA != null) {
                                    Annot linkedAnnot = new Annot(cmdTA);
                                    if (linkedAnnot.isValid()) {
                                        mNextToolMode = ToolMode.RICH_MEDIA;
                                        RichMedia tool = (RichMedia) ((ToolManager) mPdfViewCtrl.getToolManager()).createTool(ToolMode.RICH_MEDIA, this);
                                        ((ToolManager) mPdfViewCtrl.getToolManager()).setTool(tool);
                                        tool.handleRichMediaAnnot(linkedAnnot, linkedAnnot.getPage().getIndex());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (PDFNetException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getMediaCmd(Annot annot) throws PDFNetException {
        Obj aso = annot.getSDFObj();
        if (null != aso &&
                aso.findObj("A") != null &&
                aso.findObj("A").findObj("CMD") != null &&
                aso.findObj("A").findObj("CMD").findObj("C") != null) {
            Obj cmdC = aso.findObj("A").findObj("CMD").findObj("C");
            if (cmdC.isString()) {
                return cmdC.getAsPDFText();
            }
        }
        return null;
    }

    private static Obj getLinkedMedia(Annot annot) throws PDFNetException {
        Obj aso = annot.getSDFObj();
        if (null != aso &&
                aso.findObj("A") != null &&
                aso.findObj("A").findObj("TA") != null &&
                aso.findObj("A").findObj("TA").findObj("Type") != null) {
            Obj cmdTA = aso.findObj("A").findObj("TA");
            Obj cmdTAType = aso.findObj("A").findObj("TA").findObj("Type");
            if (cmdTAType.isName()) {
                String name = cmdTAType.getName();
                if (name.equals("Annot")) {
                    return cmdTA;
                }
            }
            return cmdTA;
        }
        return null;
    }

    /**
     * The overload implementation of {@link Tool#onClose()}.
     */
    @Override
    public void onClose() {
        super.onClose();
        applyFormFieldEditBoxAndQuit(true);
        closeAllDialogs();
    }

    private SimpleDateTimePickerFragment getDialog() {
        Activity activity = ((ToolManager) mPdfViewCtrl.getToolManager()).getCurrentActivity();
        if (activity != null) {
            Fragment fragment = ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag(PICKER_TAG);
            if (fragment != null && fragment instanceof SimpleDateTimePickerFragment) {
                return ((SimpleDateTimePickerFragment) fragment);
            }
        }
        return null;
    }

    private void closeAllDialogs() {
        if (getDialog() != null) {
            getDialog().dismiss();
        }
    }

    private boolean isDialogShowing() {
        return getDialog() != null && getDialog().isAdded();
    }

    /**
     * The overload implementation of {@link Tool#onSingleTapConfirmed(MotionEvent)}.
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return handleForm(e, null);
    }

    /**
     * The overload implementation of {@link Tool#onPostSingleTapConfirmed()}.
     */
    @Override
    public void onPostSingleTapConfirmed() {
        if (mEditor == null) {
            // Inline edit box is a widget added to PDFViewCtrl for edit text inline.
            // If it is null, it implies the user has single-tapped on other forms, such
            // as choice, button, etc. In such cases, we return to the pan mode immediately.
            safeSetNextToolMode();
        }
    }

    /**
     * The overload implementation of {@link Tool#onPageTurning(int, int)}.
     */
    @Override
    public void onPageTurning(int old_page, int cur_page) {
        safeSetNextToolMode();
        if (mAnnot != null && mEditor != null) {
            // In non-continuous mode and switched to a different page, apply the
            // Necessary changes to forms.
            applyFormFieldEditBoxAndQuit(true);
        }
    }

    /**
     * The overload implementation of {@link Tool#onLayout(boolean, int, int, int, int)}.
     */
    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAnnot != null && mPdfViewCtrl != null) {
            if (!mPdfViewCtrl.isContinuousPagePresentationMode(mPdfViewCtrl.getPagePresentationMode())) {
                if (mAnnotPageNum != mPdfViewCtrl.getCurrentPage()) {
                    // Now in single page mode, and the annotation is not on this page,
                    // quit this tool mode.
                    if (mEditor != null) {
                        applyFormFieldEditBoxAndQuit(true);
                    }
                    unsetAnnot();
                    safeSetNextToolMode();
                }
            }
        }
    }

    /**
     * The overload implementation of {@link Tool#onLongPress(MotionEvent)}.
     */
    @Override
    public boolean onLongPress(MotionEvent e) {
        return handleForm(e, null);
    }

    /**
     * Tabs to the next field.
     *
     * @param pageNum The page number
     * @return True if successful
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean tabToNextField(int pageNum) {
        if (mAnnot == null || mPdfViewCtrl == null) {
            return false;
        }

        try {
            Page page = mPdfViewCtrl.getDoc().getPage(pageNum);
            int annotCount = page.getNumAnnots();
            boolean currentAnnotFound = false;
            for (int i = 0; i < annotCount; i++) {
                Annot annot = page.getAnnot(i);
                Rect annotRect = annot.getRect();
                Rect toolAnnotRect = mAnnot.getRect();
                if (annotRect.getX1() == toolAnnotRect.getX1() && annotRect.getY1() == toolAnnotRect.getY1()) {
                    currentAnnotFound = true;
                    continue;
                }
                if (!currentAnnotFound)
                    continue;
                if (annot.getType() != Annot.e_Widget)
                    continue;
                Widget annotWidget = new Widget(annot);
                Field annotWidgetField = annotWidget.getField();
                if (annotWidgetField == null || !annotWidgetField.isValid() || annotWidgetField.getFlag(Field.e_read_only) || annotWidgetField.getType() != Field.e_text)
                    continue;
                mHasClosed = false;
                applyFormFieldEditBoxAndQuit(false);
                mHasClosed = false;
                handleForm(null, annot);
                setAnnot(annot, pageNum);
                buildAnnotBBox();
                handleForm(null, annot);
                return true;
            }
        } catch (PDFNetException e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
        applyFormFieldEditBoxAndQuit(true);
        return false;
    }

    private boolean handleForm(MotionEvent e, Annot annot) {
        boolean handled = false;
        int x = 0;
        int y = 0;
        if (annot == null) {
            x = (int) (e.getX() + 0.5);
            y = (int) (e.getY() + 0.5);
        }

        if (mAnnot != null && mPdfViewCtrl != null) {
            mNextToolMode = ToolMode.FORM_FILL;

            Annot tempAnnot = null;
            boolean isAnnotValid = false;
            int annotType = -1;
            boolean shouldUnlockRead = false;
            try {
                mPdfViewCtrl.docLockRead();
                shouldUnlockRead = true;
                if (annot == null) {
                    tempAnnot = mPdfViewCtrl.getAnnotationAt(x, y);
                } else {
                    tempAnnot = annot;
                }
                isAnnotValid = tempAnnot.isValid();
                if (isAnnotValid) {
                    annotType = tempAnnot.getType();
                }
            } catch (Exception ex) {
                AnalyticsHandlerAdapter.getInstance().sendException(ex);
            } finally {
                if (shouldUnlockRead) {
                    mPdfViewCtrl.docUnlockRead();
                }
            }

            if (mAnnot.equals(tempAnnot)) {
                handled = handleWidget();
            } else {
                // Otherwise goes back to the pan mode.
                if (mEditor != null) {
                    boolean hideKeyboard = true;
                    if (isAnnotValid && annotType == Annot.e_Widget) {
                        hideKeyboard = false;
                    }
                    // do not hide the keyboard if continue to edit the next field
                    applyFormFieldEditBoxAndQuit(hideKeyboard);
                }
                unsetAnnot();
                safeSetNextToolMode();
            }
        }
        return handled;
    }

    private boolean handleWidget() {
        return handleWidget(mAnnot, mAnnotPageNum);
    }

    private boolean handleWidget(Annot annot, int pageNum) {
        if (onInterceptAnnotationHandling(annot)) {
            return true;
        }
        if (annot == null) {
            return false;
        }
        boolean shouldUnlock = false;
        boolean handled = false;
        boolean hasModification = false;
        boolean hasOnlyExecutionChanges = false;
        try {
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            raiseAnnotationPreModifyEvent(annot, pageNum);
            executeAction(annot, Annot.e_action_trigger_annot_enter);
            executeAction(annot, Annot.e_action_trigger_annot_down);
            executeAction(annot, Annot.e_action_trigger_annot_focus);
            Widget w = new Widget(annot);
            mField = w.getField();
            if (mField != null && mField.isValid() && !mField.getFlag(Field.e_read_only)) {
                int field_type = mField.getType();

                if (field_type == Field.e_check) {
                    ViewChangeCollection view_change = mField.setValue(!mField.getValueAsBool());
                    mPdfViewCtrl.refreshAndUpdate(view_change);
                    executeAction(mField, Annot.e_action_trigger_annot_blur);
                    executeAction(mField, Annot.e_action_trigger_annot_exit);
                    hasModification = true;
                } else if (field_type == Field.e_radio) {
                    if (!mField.getValueAsBool()) {
                        ViewChangeCollection view_change = mField.setValue(true);
                        mPdfViewCtrl.refreshAndUpdate(view_change);
                        executeAction(mField, Annot.e_action_trigger_annot_blur);
                        executeAction(mField, Annot.e_action_trigger_annot_exit);
                        hasModification = true;
                    }
                } else if (field_type == Field.e_button) {
                    safeSetNextToolMode();
                    handled = true;
                } else if (field_type == Field.e_choice) {
                    final DialogFormFillChoice d = new DialogFormFillChoice(mPdfViewCtrl, annot, pageNum);
                    d.show();
                } else if (field_type == Field.e_text) {
                    mIsMultiLine = mField.getFlag(Field.e_multiline);
                    boolean inline_edit = canUseInlineEditing();
                    if (!inline_edit) {
                        // Pop up a dialog for inputting text
                        final DialogFormFillText d = new DialogFormFillText(mPdfViewCtrl, annot, pageNum);
                        d.show();
                    } else {
                        // Inline editing
                        handleTextInline();
                    }
                } else if (field_type == Field.e_signature) {
                    if (((ToolManager) mPdfViewCtrl.getToolManager()).isUsingDigitalSignature()) {
                        mNextToolMode = ToolMode.DIGITAL_SIGNATURE;
                    } else {
                        mNextToolMode = ToolMode.SIGNATURE;
                    }
                    if (((ToolManager) mPdfViewCtrl.getToolManager()).isToolModeDisabled((ToolMode) mNextToolMode)) {
                        // signature tool disabled, back to pan
                        safeSetNextToolMode();
                        handled = true;
                    }
                }
                executeAction(annot, Annot.e_action_trigger_activate);
                executeAction(annot, Annot.e_action_trigger_annot_up);
            }
            if (hasModification) {
                raiseAnnotationModifiedEvent(annot, pageNum);
            } else {
                hasOnlyExecutionChanges = mPdfViewCtrl.getDoc().hasChangesSinceSnapshot();
            }
        } catch (PDFNetException ex) {
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
                if (hasOnlyExecutionChanges) {
                    raiseAnnotationActionEvent();
                }
            }
        }
        return handled;
    }

    private boolean canUseInlineEditing() {
        if (mPdfViewCtrl == null) {
            return false;
        }

        try {
            if (mPdfViewCtrl.getPageRotation() != Page.e_0 && mPdfViewCtrl.getPageRotation() != Page.e_180) {
                // do not support inline text editing if the document is rotated 90 or 270 degree
                return false;
            }
            float font_sz = 12 * (float) mPdfViewCtrl.getZoom();
            GState gs = mField.getDefaultAppearance();
            if (gs != null) {
                font_sz = (float) gs.getFontSize();
                if (font_sz <= 0) {
                    if (mIsMultiLine) {
                        font_sz = 12 * (float) mPdfViewCtrl.getZoom();
                    } else {
                        // Auto size; so examine the annotation's bbox
                        double x1 = mAnnotBBox.left + mBorderWidth;
                        double y1 = mAnnotBBox.bottom - mBorderWidth;   // Note mAnnotBBox is in PDF page space, so have to reverse it
                        double x2 = mAnnotBBox.right - mBorderWidth;
                        double y2 = mAnnotBBox.top + mBorderWidth;
                        double pts1[] = mPdfViewCtrl.convPagePtToScreenPt(x1, y1, mAnnotPageNum);
                        double pts2[] = mPdfViewCtrl.convPagePtToScreenPt(x2, y2, mAnnotPageNum);
                        double height = Math.abs(pts1[1] - pts2[1]);
                        font_sz = (float) (height * 0.8f);
                    }
                } else {
                    font_sz *= (float) mPdfViewCtrl.getZoom();
                }
            }
            if (font_sz > 12) {
                // The font size is large enough, so use inline editing
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private void adjustFontSize(EditText editText) {
        if (mPdfViewCtrl != null && mField != null) {
            try {
                float font_sz = 12 * (float) mPdfViewCtrl.getZoom();
                GState gs = mField.getDefaultAppearance();
                if (gs != null) {
                    font_sz = (float) gs.getFontSize();
                    if (font_sz <= 0) {
                        // Auto size
                        font_sz = 12 * (float) mPdfViewCtrl.getZoom();
                    } else {
                        font_sz *= (float) mPdfViewCtrl.getZoom();
                    }
                }

                editText.setPadding(0, 0, 0, 0);
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_sz);
            } catch (PDFNetException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    private void mapColorFont(EditText editText) {
        if (mField != null) {
            try {
                GState gs = mField.getDefaultAppearance();
                if (gs != null) {
                    // Set text color
                    ColorPt color = gs.getFillColor();
                    color = gs.getFillColorSpace().convert2RGB(color);
                    int r = (int) Math.floor(color.get(0) * 255 + 0.5);
                    int g = (int) Math.floor(color.get(1) * 255 + 0.5);
                    int b = (int) Math.floor(color.get(2) * 255 + 0.5);
                    int color_int = Color.argb(255, r, g, b);
                    editText.setTextColor(color_int);

                    // Set background color
                    color = getFieldBkColor();
                    if (color == null) {
                        r = 255;
                        g = 255;
                        b = 255;
                    } else {
                        r = (int) Math.floor(color.get(0) * 255 + 0.5);
                        g = (int) Math.floor(color.get(1) * 255 + 0.5);
                        b = (int) Math.floor(color.get(2) * 255 + 0.5);
                    }
                    color_int = Color.argb(25, r, g, b);
                    editText.setBackgroundColor(color_int);

                    // Set the font of the EditBox to match the PDF form field's. In order to do this,
                    // you need to bundle with you App the fonts, such as "Times", "Arial", "Courier",
                    // "Helvetica", etc. The following is just a place holder.
                    Font font = gs.getFont();
                    if (font != null) {
                        String family_name = font.getFamilyName();
                        if (family_name == null || family_name.length() == 0) {
                            family_name = "Times";
                        }
                        String name = font.getName();
                        if (name == null || name.length() == 0) {
                            name = "Times New Roman";
                        }
                        if (family_name.contains("Times") || name.contains("Times")) {
                            // NOTE: you need to bundle the font file in you App and use it here.
                            //TypeFace tf == Typeface.create(...);
                            //mInlineEditBox.setTypeface(tf);
                        }
                    }
                }
            } catch (PDFNetException e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }
    }

    private ColorPt getFieldBkColor() {
        if (mAnnot != null) {
            try {
                Obj o = mAnnot.getSDFObj().findObj("MK");
                if (o != null) {
                    Obj bgc = o.findObj("BG");
                    if (bgc != null && bgc.isArray()) {
                        int sz = (int) bgc.size();
                        switch (sz) {
                            case 1:
                                Obj n = bgc.getAt(0);
                                if (n.isNumber()) {
                                    return new ColorPt(n.getNumber(), n.getNumber(), n.getNumber());
                                }
                                break;
                            case 3:
                                Obj r = bgc.getAt(0), g = bgc.getAt(1), b = bgc.getAt(2);
                                if (r.isNumber() && g.isNumber() && b.isNumber()) {
                                    return new ColorPt(r.getNumber(), g.getNumber(), b.getNumber());
                                }
                                break;
                            case 4:
                                Obj c = bgc.getAt(0), m = bgc.getAt(1), y = bgc.getAt(2), k = bgc.getAt(3);
                                if (c.isNumber() && m.isNumber() && y.isNumber() && k.isNumber()) {
                                    ColorPt cp = new ColorPt(c.getNumber(), m.getNumber(), y.getNumber(), k.getNumber());
                                    ColorSpace cs = ColorSpace.createDeviceCMYK();
                                    return cs.convert2RGB(cp);
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            }
        }

        return null;
    }

    private void handleTextInline() {
        try {
            if (mAnnot != null && mField != null && mField.isValid()) {
                int max_len = mField.getMaxLen();

                if (mEditor != null) {
                    applyFormFieldEditBoxAndQuit(false);
                }

                // hide field
                mPdfViewCtrl.hideAnnotation(mAnnot);
                mPdfViewCtrl.update(mAnnot, mAnnotPageNum);

                mEditor = new AutoScrollEditor(mPdfViewCtrl.getContext());
                mEditor.getEditText().setSingleLine(!mIsMultiLine);
                mEditor.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mEditor.getEditText().addTextChangedListener(new TextWatcher() {
                    String originalText = "";
                    String newText = "";
                    int cursorPosition;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        originalText = s.toString();
                        if (mEditor != null) {
                            cursorPosition = mEditor.getEditText().getSelectionStart();
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            newText = s.toString();
                            if (newText.equals(originalText)) return;
                            Obj aa = mField.getTriggerAction(Field.e_action_trigger_keystroke);
                            if (aa != null) {
                                Action a;
                                a = new Action(aa);
                                int to = start + before;
                                String fieldName = mField.getName();
                                String changedText = s.toString();
                                String addedText = changedText.substring(start, start + count);
                                KeyStrokeEventData data;
                                data = new KeyStrokeEventData(fieldName, originalText, addedText, start, to);
                                KeyStrokeActionResult actionResult;
                                actionResult = a.executeKeyStrokeAction(data);
                                if (actionResult.isValid()) {
                                    String addedValue = actionResult.getText();
                                    if (!addedValue.equals(addedText)) {
                                        String textBefore = originalText.substring(0, start);
                                        String textAfter = originalText.substring(to, originalText.length());
                                        newText = textBefore + addedValue + textAfter;
                                        mEditor.getEditText().setTextKeepState(newText);
                                    }
                                } else {
                                    newText = originalText;
                                    mEditor.getEditText().setTextKeepState(newText);
                                    if (cursorPosition < newText.length()) {
                                        mEditor.getEditText().setSelection(cursorPosition);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e,
                                    "originalText:" + originalText + ",newText:" + newText
                                            + ", cursorPosition:" + cursorPosition);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                if (mAnnot == null) {
                    return;
                }
                // Compute border width
                Annot.BorderStyle bs = mAnnot.getBorderStyle();
                mBorderWidth = bs.getWidth();

                if (bs.getStyle() == Annot.BorderStyle.e_beveled || bs.getStyle() == Annot.BorderStyle.e_inset) {
                    mBorderWidth = bs.getWidth() * 2;
                }

                Obj trigAction = mField.getTriggerAction(Field.e_action_trigger_keystroke);
                boolean isNumberField = false;

                if (trigAction != null && trigAction.isDict()) {
                    Obj js = trigAction.findObj("JS");
                    if (js != null && js.isString()) {
                        String jsStr = js.getAsPDFText();
                        if (jsStr.contains("AFNumber") || jsStr.contains("AFPercent")) {
                            isNumberField = true;
                        }
                    }
                }
                if (isNumberField) {
                    mEditor.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }

                String jsStr = null;
                boolean isDate = false;
                boolean isTime = false;

                if (trigAction != null && trigAction.isDict()) {
                    Obj js = trigAction.findObj("JS");
                    if (js != null && js.isString()) {
                        jsStr = js.getAsPDFText();
                        if (jsStr.contains("AFDate")) {
                            isDate = true;
                        } else if (jsStr.contains("AFTime")) {
                            isTime = true;
                        }
                    }
                }

                final String formatString = jsStr;
                if ((isDate || isTime) && mCanUseDateTimePicker) {
                    Activity activity = ((ToolManager) mPdfViewCtrl.getToolManager()).getCurrentActivity();
                    if (activity != null) {
                        int mode = isDate ? SimpleDateTimePickerFragment.MODE_DATE : SimpleDateTimePickerFragment.MODE_TIME;
                        SimpleDateTimePickerFragment picker = SimpleDateTimePickerFragment.newInstance(mode);
                        picker.setSimpleDatePickerListener(new SimpleDateTimePickerFragment.SimpleDatePickerListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                try {
                                    String format = Utils.getDateTimeFormatFromField(formatString, true);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);

                                    Calendar cal = Calendar.getInstance();
                                    cal.set(view.getYear(), view.getMonth(), view.getDayOfMonth());
                                    String dateStr = dateFormat.format(cal.getTime());

                                    applyFormFieldEditBoxAndQuit(false, dateStr);
                                } catch (Exception e) {
                                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                                }
                            }

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                try {
                                    String format = Utils.getDateTimeFormatFromField(formatString, false);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);

                                    Calendar cal = Calendar.getInstance();
                                    int year = cal.get(Calendar.YEAR);
                                    int month = cal.get(Calendar.MONTH);
                                    int day = cal.get(Calendar.DAY_OF_MONTH);
                                    cal.set(year, month, day, hourOfDay, minute);
                                    String dateStr = dateFormat.format(cal.getTime());

                                    applyFormFieldEditBoxAndQuit(false, dateStr);
                                } catch (Exception e) {
                                    AnalyticsHandlerAdapter.getInstance().sendException(e);
                                }
                            }

                            @Override
                            public void onClear() {
                                applyFormFieldEditBoxAndQuit(false, "");
                            }

                            @Override
                            public void onDismiss(boolean manuallyEnterValue, boolean dismissedWithNoSelection) {
                                if (dismissedWithNoSelection) {
                                    applyFormFieldEditBoxAndQuit(false);
                                }
                                mCanUseDateTimePicker = !manuallyEnterValue;
                                mEditor = null;
                                if (manuallyEnterValue) {
                                    handleTextInline();
                                }
                            }
                        });
                        picker.show(((FragmentActivity) activity).getSupportFragmentManager(), PICKER_TAG);
                        return;
                    }
                }

                // Comb and max length
                if (max_len > 0) {
                    LengthFilter filters[] = new LengthFilter[1];
                    filters[0] = new InputFilter.LengthFilter(max_len);
                    mEditor.getEditText().setFilters(filters);
                }

                // Password format
                if (mField.getFlag(Field.e_password)) {
                    mEditor.getEditText().setTransformationMethod(new PasswordTransformationMethod());
                }

                // Set initial text
                String init_str = mField.getValueAsString();
                mEditor.getEditText().setText(init_str);

                // Compute alignment
                int just = mField.getJustification();
                int gravityVertical = Gravity.TOP;
                if (!mIsMultiLine) {
                    gravityVertical = Gravity.CENTER_VERTICAL;
                }
                if (just == Field.e_left_justified) {
                    mEditor.setGravity(Gravity.START | gravityVertical);
                    if (Utils.isJellyBeanMR1()) {
                        mEditor.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    }
                } else if (just == Field.e_centered) {
                    mEditor.setGravity(Gravity.CENTER | gravityVertical);
                    if (Utils.isJellyBeanMR1()) {
                        mEditor.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                } else if (just == Field.e_right_justified) {
                    mEditor.setGravity(Gravity.END | gravityVertical);
                    if (Utils.isJellyBeanMR1()) {
                        mEditor.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    }
                }
                mEditor.setAnnot(mPdfViewCtrl, mAnnot, mAnnotPageNum);
                ViewerUtils.scrollToAnnotRect(mPdfViewCtrl, mAnnot.getRect(), mAnnotPageNum);

                // Compute font size
                adjustFontSize(mEditor.getEditText());

                // Set color and font
                mapColorFont(mEditor.getEditText());

                // Bring it up
                mPdfViewCtrl.addView(mEditor);
                mEditor.getEditText().requestFocus();

                mEditor.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            String str = textView.getText().toString();
                            applyFormFieldEditBoxAndQuit(true, str);
                            return true;
                        } else if (i == EditorInfo.IME_ACTION_NEXT) {
                            return tabToNextField(mPdfViewCtrl.getCurrentPage());
                        }
                        return false;
                    }
                });

                mEditor.getEditText().setAutoScrollEditTextListener(new AutoScrollEditText.AutoScrollEditTextListener() {
                    @Override
                    public boolean onKeyUp(int keyCode, KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP) {
                            applyFormFieldEditBoxAndQuit(true);
                            return true;
                        }
                        if (!mIsMultiLine &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            applyFormFieldEditBoxAndQuit(true);
                            return true;
                        }
                        if (ShortcutHelper.isSwitchForm(keyCode, event)) {
                            tabToNextField(mPdfViewCtrl.getCurrentPage());
                        }
                        return true;
                    }

                    @Override
                    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
                        return false;
                    }
                });

                // Bring up soft keyboard in case it is not shown automatically
                InputMethodManager imm = (InputMethodManager) mPdfViewCtrl.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(mEditor.getEditText(), 0);
                }
            }
        } catch (PDFNetException e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    /**
     * The overload implementation of {@link Tool#onScale(float, float)}.
     */
    @Override
    public boolean onScale(float x, float y) {
        if (mEditor != null) {
            adjustFontSize(mEditor.getEditText());
        }
        return false;
    }

    /**
     * The overload implementation of {@link Tool#onScaleEnd(float, float)}.
     */
    @Override
    public boolean onScaleEnd(float x, float y) {
        if (mEditor != null) {
            adjustFontSize(mEditor.getEditText());
            mEditor.getEditText().requestFocus();
        }
        return false;
    }

    /**
     * The overload implementation of {@link Tool#onDoubleTapEnd(MotionEvent)}.
     */
    @Override
    public void onDoubleTapEnd(MotionEvent e) {
        if (mEditor != null) {
            adjustFontSize(mEditor.getEditText());
            mEditor.getEditText().requestFocus();
        }
    }

    private void applyFormFieldEditBoxAndQuit(boolean hideKeyboard) {
        applyFormFieldEditBoxAndQuit(hideKeyboard, null);
    }

    private void applyFormFieldEditBoxAndQuit(final boolean hideKeyboard, String str) {
        if (!mHasClosed && mPdfViewCtrl != null && mEditor != null) {
            boolean shouldUnlock = false;
            boolean hasModification = false;
            boolean hasOnlyExecutionChanges = false;
            try {
                if (null == mAnnot || !mAnnot.isValid()) {
                    return;
                }
                mPdfViewCtrl.docLock(true);
                shouldUnlock = true;
                if (str == null && !isDialogShowing()) {
                    str = mEditor.getEditText().getText().toString();
                }
                if (str != null) {
                    if (!mField.getValueAsString().equals(str)) {
                        hasModification = true;
                    }

                    if (hasModification) {
                        raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
                        Widget widget = new Widget(mAnnot);
                        updateFont(mPdfViewCtrl, widget, str);

                        ViewChangeCollection view_change = mField.setValue(str);
                        mPdfViewCtrl.refreshAndUpdate(view_change);

                        if (mUseEditTextAppearance) {
                            // apply edit text appearance instead
                            final long annotImpl = mAnnot.__GetHandle();
                            final int annotPageNum = mAnnotPageNum;
                            mEditor.getEditText().setCursorVisible(false);
                            mEditor.getEditText().clearComposingText();

                            Annot annot = Annot.__Create(annotImpl, mPdfViewCtrl.getDoc());
                            setAnnot(annot, annotPageNum);
                            com.pdftron.pdf.Rect annotRectScreen = mPdfViewCtrl.getScreenRectForAnnot(annot, annotPageNum);
                            RectF pageRectScreen = Utils.buildPageBoundBoxOnClient(mPdfViewCtrl, annotPageNum);
                            File ret = createPdfFromView(mEditor.getEditText(), annotRectScreen.getWidth(), annotRectScreen.getHeight(),
                                    pageRectScreen.width(), pageRectScreen.height());
                            if (ret != null) {
                                refreshCustomWidgetAppearance(ret, annot);
                                mPdfViewCtrl.update(annot, annotPageNum);
                            }
                        }
                    }

                    executeAction(mField, Annot.e_action_trigger_annot_blur);
                    executeAction(mField, Annot.e_action_trigger_annot_exit);
                }
                hasOnlyExecutionChanges = postApplyFormFieldEditBoxAndQuit(hideKeyboard, hasModification, mAnnot, mAnnotPageNum);

                // show field
                mPdfViewCtrl.showAnnotation(mAnnot);
                mPdfViewCtrl.update(mAnnot, mAnnotPageNum);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    mPdfViewCtrl.docUnlock();
                }
                if (!mUseEditTextAppearance || !hasModification) {
                    unsetAnnot();
                    safeSetNextToolMode();
                    if (hasOnlyExecutionChanges) {
                        raiseAnnotationActionEvent();
                    }
                }
            }
        }
    }

    private boolean postApplyFormFieldEditBoxAndQuit(boolean hideKeyboard, boolean hasModification, Annot annot, int pageNum) throws PDFNetException {
        if (hideKeyboard) {
            // Hide soft keyboard
            InputMethodManager imm = (InputMethodManager) mPdfViewCtrl.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mEditor.getEditText().getWindowToken(), 0);
            }
        }

        mPdfViewCtrl.removeView(mEditor);
        mEditor = null;

        boolean hasOnlyExecutionChanges = false;

        if (hasModification) {
            raiseAnnotationModifiedEvent(annot, pageNum);
        } else {
            hasOnlyExecutionChanges = mPdfViewCtrl.getDoc().hasChangesSinceSnapshot();
        }

        mHasClosed = true;

        return hasOnlyExecutionChanges;
    }

    private void safeSetNextToolMode() {
        if (mForceSameNextToolMode) {
            mNextToolMode = mCurrentDefaultToolMode;
        } else {
            mNextToolMode = ToolMode.PAN;
        }
    }

    private static boolean refreshCustomWidgetAppearance(
            @NonNull File appearance,
            @NonNull Annot annot) {

        PDFDoc template = null;
        try {
            Widget widget = new Widget(annot);
            template = new PDFDoc(appearance.getAbsolutePath());
            Page editTextPage = template.getPage(1);
            com.pdftron.sdf.Obj contents = editTextPage.getContents();
            com.pdftron.sdf.Obj importedContents = annot.getSDFObj().getDoc().importObj(contents, true);
            com.pdftron.pdf.Rect bbox = editTextPage.getMediaBox();
            importedContents.putRect("BBox", bbox.getX1(), bbox.getY1(), bbox.getX2(), bbox.getY2());
            importedContents.putName("Subtype", "Form");
            importedContents.putName("Type", "XObject");

            com.pdftron.sdf.Obj res = editTextPage.getResourceDict();
            if (res != null) {
                com.pdftron.sdf.Obj importedRes = annot.getSDFObj().getDoc().importObj(res, true);
                importedContents.put("Resources", importedRes);
            }

            // set the appearance of rubber stamp icon to the custom icon
            widget.setAppearance(importedContents);
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            Utils.closeQuietly(template);
        }

        return false;
    }

    private static int convPixelToPoint(double pixel) {
        return (int) (pixel * 72 / 96 + 0.5);
    }

    private static File createPdfFromView(View content, double annotPixWidth, double annotPixHeight,
            double pagePixWidth, double pagePixHeight) {
        if (!Utils.isKitKat()) {
            return null;
        }

        try {
            // create a new document
            PdfDocument document = new PdfDocument();

            PdfDocument.PageInfo pageInfo = (new PdfDocument.PageInfo.Builder(
                    ((int) (content.getScrollX() + content.getWidth())),
                    ((int) (content.getScrollY() + content.getHeight())), 1))
                    .create();

            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // draw something on the page

            content.draw(page.getCanvas());

            // finish the page
            document.finishPage(page);
            // add more pages
            // write the document content

            File testFile = new File(content.getContext().getFilesDir(), "form-EditText.pdf");
            testFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(testFile);

            document.writeTo(fileOutputStream);

            // close the document
            document.close();

            // crop
            PDFDoc pdfDoc = new PDFDoc(testFile.getAbsolutePath());
            Page p1 = pdfDoc.getPage(1);
            Rect visible = p1.getVisibleContentBox();
            p1.setMediaBox(visible);
            p1.setCropBox(visible);
            pdfDoc.save();

            return testFile;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Annot getAnnot() {
        return mAnnot;
    }

    public int getPage() {
        return mAnnotPageNum;
    }

    public boolean isEditing() {
        return mEditor != null;
    }
}
