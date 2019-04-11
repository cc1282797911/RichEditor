package com.zjrb.editor.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zjrb.editor.R;
import com.zjrb.editor.RichEditor;
import com.zjrb.editor.adapter.MaterialsMenuAdapter;
import com.zjrb.editor.bean.MaterialsMenuBean;
import com.zjrb.editor.config.EditorOpType;
import com.zjrb.editor.config.MaterialsMenuType;
import com.zjrb.editor.interfaces.OnColorSelectListener;
import com.zjrb.editor.interfaces.OnDecorationStateListener;
import com.zjrb.editor.interfaces.OnMaterialsItemClickListener;
import com.zjrb.editor.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 富文本编辑器的菜单
 *
 * Created by yyp on 2019/2/27
 */
public class EditorOpMenuView extends FrameLayout implements View.OnClickListener{

    private final static String TAG = "EditorOpMenuView";

    private Context mContext;
    private RichEditor mRichEditor;
    private OnMaterialsItemClickListener mOnMaterialsItemClickListener;

    private ImageButton mBoldView;
    private ImageButton mItalicView;
    private ImageButton mUnderlineView;
    private View mTextSizeContainer;
    private TextView mTextSizeView;
    private ImageButton mTextColorView;
    private ImageButton mMaterialsView;
    private ImageButton mOrderedListView;
    private ImageButton mAlignLeftView;
    private ImageButton mAlignCenterView;
    private ImageButton mAlignRightView;

    private RecyclerView mMaterialsMenuView;
    private MaterialsMenuAdapter mMaterialsMenuAdapter;

    // 图标颜色状态
    private final static ColorStateList sColorStateList;
    static {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected}, // 选中
                new int[] { -android.R.attr.state_selected} // 未选中
        };

        int[] colors = new int[] {
                Color.parseColor("#4786ff"),
                Color.parseColor("#68696e")
        };
        sColorStateList = new ColorStateList(states, colors);
    }

    public EditorOpMenuView(Context context) {
        this(context, null);
    }

    public EditorOpMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditorOpMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    /**
     * 初始化控件
     *
     * @param context
     */
    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_editor_layout_editor_op_menu, null);
        mBoldView = view.findViewById(R.id.editor_action_bold);
        mItalicView = view.findViewById(R.id.editor_action_italic);
        mUnderlineView = view.findViewById(R.id.editor_action_underline);
        mTextSizeContainer = view.findViewById(R.id.editor_action_font_size);
        mTextSizeView = view.findViewById(R.id.editor_font_size);
        mTextColorView = view.findViewById(R.id.editor_action_font_color);
        mMaterialsView = view.findViewById(R.id.editor_action_materials);
        mOrderedListView = view.findViewById(R.id.editor_action_ordered_list);
        mAlignLeftView = view.findViewById(R.id.editor_action_justify_left);
        mAlignRightView = view.findViewById(R.id.editor_action_justify_right);
        mAlignCenterView = view.findViewById(R.id.editor_action_justify_center);
        mMaterialsMenuView = view.findViewById(R.id.rv_editor_materials_menu);

        //监听点击事件
        mBoldView.setOnClickListener(this);
        mItalicView.setOnClickListener(this);
        mUnderlineView.setOnClickListener(this);
        mTextSizeContainer.setOnClickListener(this);
        mTextColorView.setOnClickListener(this);
        mMaterialsView.setOnClickListener(this);
        mOrderedListView.setOnClickListener(this);
        mAlignLeftView.setOnClickListener(this);
        mAlignRightView.setOnClickListener(this);
        mAlignCenterView.setOnClickListener(this);

        //处理图标选中状态
        ImageUtils.setTintList(mBoldView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mItalicView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mUnderlineView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mOrderedListView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mAlignLeftView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mAlignRightView.getDrawable(), sColorStateList);
        ImageUtils.setTintList(mAlignCenterView.getDrawable(), sColorStateList);

        initMaterialsMenuView();
        addView(view);
    }

    /**
     * 初始化素材菜单
     */
    private void initMaterialsMenuView(){
        //增加素材菜单item
        List<MaterialsMenuBean> materialsMenuBeans = new ArrayList<>();
        materialsMenuBeans.add(new MaterialsMenuBean(
                MaterialsMenuType.MATERIALS_IMAGE,
                R.drawable.module_editor_ic_materials_image,
                mContext.getResources().getString(R.string.editor_materials_img)));
        materialsMenuBeans.add(new MaterialsMenuBean(
                MaterialsMenuType.MATERIALS_VIDEO,
                R.drawable.module_editor_ic_materials_video,
                mContext.getResources().getString(R.string.editor_materials_video)));
        materialsMenuBeans.add(new MaterialsMenuBean(
                MaterialsMenuType.MATERIALS_TXT,
                R.drawable.module_editor_ic_materials_txt,
                mContext.getResources().getString(R.string.editor_materials_txt)));
        materialsMenuBeans.add(new MaterialsMenuBean(
                MaterialsMenuType.LOCAL_IMAGE,
                R.drawable.module_editor_ic_local_image,
                mContext.getResources().getString(R.string.editor_local_img)));
        materialsMenuBeans.add(new MaterialsMenuBean(
                MaterialsMenuType.LOCAL_VIDEO,
                R.drawable.module_editor_ic_local_video,
                mContext.getResources().getString(R.string.editor_local_video)));
        mMaterialsMenuAdapter = new MaterialsMenuAdapter(materialsMenuBeans);
        mMaterialsMenuView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mMaterialsMenuView.setAdapter(mMaterialsMenuAdapter);
        //item点击回调
        mMaterialsMenuAdapter.setOnMaterialsItemClickListener(new OnMaterialsItemClickListener() {

            @Override
            public void onMaterialsItemClick(MaterialsMenuBean bean) {
                if(mOnMaterialsItemClickListener != null){
                    mOnMaterialsItemClickListener.onMaterialsItemClick(bean);
                }
            }
        });
    }

    /**
     * 设置素材菜单item点击监听
     *
     * @param onMaterialsItemClickListener .
     */
    public void setOnMaterialsItemClickListener(OnMaterialsItemClickListener onMaterialsItemClickListener) {
        this.mOnMaterialsItemClickListener = onMaterialsItemClickListener;
    }

    /**
     * 打开第三方缓存
     *
     * @param webView WebView对象
     */
    private void enableWebViewCookie(WebView webView) {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    /**
     * 其他输入框拿到光标后，菜单隐藏
     *
     * @param editText 其他输入框
     */
    public void hideWhenViewFocused(EditText editText) {
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    EditorOpMenuView.this.setVisibility(GONE);
                }
            }
        });
    }

    /**
     * 绑定RichEditor
     *
     * @param mRichEditor RichEditor对象
     */
    public void setRichEditor(final RichEditor mRichEditor) {
        this.mRichEditor = mRichEditor;
        if (mRichEditor != null) {
            enableWebViewCookie(mRichEditor);
            mRichEditor.setOnDecorationChangeListener(new OnDecorationStateListener() {
                @Override
                public void onStateChange(String text, List<EditorOpType> types) {
                    if (types == null) {
                        return;
                    }

                    //处理操作图标变色
                    boolean isBold = false;
                    boolean isItalic = false;
                    boolean isUnderLine = false;
                    boolean isOrderedList = false;
                    for (EditorOpType type : types) {
                        switch (type) {
                            case BOLD:
                                isBold = true;
                                break;
                            case ITALIC:
                                isItalic = true;
                                break;
                            case UNDERLINE:
                                isUnderLine = true;
                                break;
                            case ORDEREDLIST:
                                isOrderedList = true;
                                break;
                            case JUSTIFYLEFT:
                                setAlignSelect(R.id.editor_action_justify_left);
                                break;
                            case JUSTIFYRIGHT:
                                setAlignSelect(R.id.editor_action_justify_right);
                                break;
                            case JUSTIFYCENTER:
                                setAlignSelect(R.id.editor_action_justify_center);
                                break;
                            case FORECOLOR:
                                setForeColorSelect(Color.parseColor(type.getValue().toString()));
                                break;
                            case FONTSIZE:
                                setFontSizeSelect(Integer.parseInt(type.getValue().toString()));
                                break;
                            default:
                                break;
                        }
                    }
                    setBoldSelect(isBold);
                    setItalicSelect(isItalic);
                    setUnderlineSelect(isUnderLine);
                    setOrderedList(isOrderedList);
                }
            });
        }
    }

    /**
     * 4个对齐方式互斥
     *
     * @param id 对齐方式按钮id
     */
    private void setAlignSelect(@IdRes int id) {
        if(id == R.id.editor_action_justify_left) {
            mAlignLeftView.setSelected(true);
            mAlignRightView.setSelected(false);
            mAlignCenterView.setSelected(false);
        }
        if(id == R.id.editor_action_justify_right) {
            mAlignLeftView.setSelected(false);
            mAlignRightView.setSelected(true);
            mAlignCenterView.setSelected(false);
        }
        if(id == R.id.editor_action_justify_center) {
            mAlignLeftView.setSelected(false);
            mAlignRightView.setSelected(false);
            mAlignCenterView.setSelected(true);
        }
    }

    /**
     * 设置选中粗体
     *
     * @param select 是否选中
     */
    private void setBoldSelect(boolean select) {
        mBoldView.setSelected(select);
    }

    /**
     * 设置选中斜体
     *
     * @param select 是否选中
     */
    private void setItalicSelect(boolean select) {
        mItalicView.setSelected(select);
    }

    /**
     * 设置选中下划线
     *
     * @param select 是否选中
     */
    private void setUnderlineSelect(boolean select) {
        mUnderlineView.setSelected(select);
    }

    /**
     * 设置选中有序列表
     *
     * @param select 是否选中
     */
    private void setOrderedList(boolean select) {
        mOrderedListView.setSelected(select);
    }

    /**
     * 设置选中的字体颜色
     *
     * @param color 字体颜色
     */
    private void setForeColorSelect(@ColorInt int color) {
        mTextColorView.setTag(color);
        mTextColorView.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 设置选中的文字大小
     *
     * @param size 文字大小 1-7
     */
    private void setFontSizeSelect(@IntRange(from = 1, to = 7) int size) {
        mTextSizeView.setTag(size);
        String[] fontSizeArr = getResources().getStringArray(R.array.editor_font_size_arr);
        mTextSizeView.setText(fontSizeArr[size-1]); //size-1 因为fontSizeArr从0开始的
    }

    /**
     * 选择文字大小
     */
    private void selectFontSize(){
        if (mRichEditor != null) {
            mRichEditor.setFontSize(5); //24px
        }
        setFontSizeSelect(5);
    }

    /**
     * 选择文字颜色
     */
    private void selectForeColor(){

        new ColorSelectDialog(mContext)
                .setTitle(mContext.getString(R.string.editor_select_color))
//                .setColors(int[], )
                .setOnColorSelectListener(new OnColorSelectListener() {
                    @Override
                    public void onColorSelect(int color) {
                        if (mRichEditor != null) {
                            mRichEditor.setTextColor(color);
                        }
                        setForeColorSelect(color);
                    }
                }).show();
    }

    /**
     * 处理菜单的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.editor_action_bold){
            if (mRichEditor != null) {
                mRichEditor.setBold();
            }
            setBoldSelect(!view.isSelected());
        }
        if(id == R.id.editor_action_italic){
            if (mRichEditor != null) {
                mRichEditor.setItalic();
            }
            setItalicSelect(!view.isSelected());
        }
        if(id == R.id.editor_action_underline){
            if (mRichEditor != null) {
                mRichEditor.setUnderline();
            }
            setUnderlineSelect(!view.isSelected());
        }
        if(id == R.id.editor_action_font_size){ //选择文字大小
            selectFontSize();
        }
        if(id == R.id.editor_action_font_color){ //选择文字颜色
            selectForeColor();
        }
        if(id == R.id.editor_action_ordered_list){
            if (mRichEditor != null) {
                mRichEditor.setNumbers();
            }
            setOrderedList(!view.isSelected());
        }
        if(id == R.id.editor_action_justify_left){
            if (mRichEditor != null) {
                mRichEditor.setAlignLeft();
            }
            setAlignSelect(R.id.editor_action_justify_left);
        }
        if(id == R.id.editor_action_justify_right){
            if (mRichEditor != null) {
                mRichEditor.setAlignRight();
            }
            setAlignSelect(R.id.editor_action_justify_right);
        }
        if(id == R.id.editor_action_justify_center){
            if (mRichEditor != null) {
                mRichEditor.setAlignCenter();
            }
            setAlignSelect(R.id.editor_action_justify_center);
        }
        if(id == R.id.editor_action_materials){ //点击素材按钮
            if(mMaterialsMenuView.getVisibility() == View.VISIBLE){
                mMaterialsMenuView.setVisibility(View.GONE);
            }else{
                mMaterialsMenuView.setVisibility(View.VISIBLE);
            }
        }
    }
}