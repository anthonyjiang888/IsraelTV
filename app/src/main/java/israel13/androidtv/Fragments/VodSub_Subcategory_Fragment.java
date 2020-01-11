package israel13.androidtv.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import israel13.androidtv.Activity.HomeActivity;
import israel13.androidtv.Activity.LoginActivity;
import israel13.androidtv.Activity.VodMovieVideoPlayActivity;
import israel13.androidtv.Adapter.VodSubCategoryGridRecyclerAdapter;
import israel13.androidtv.Adapter.VodSub_SubCategoryGridAdapter;
import israel13.androidtv.Adapter.VodSub_SubCategoryGridRecyclerAdapter;
import israel13.androidtv.NetworkOperation.IJSONParseListener;
import israel13.androidtv.NetworkOperation.JSONRequestResponse;
import israel13.androidtv.NetworkOperation.MyVolley;
import israel13.androidtv.R;
import israel13.androidtv.Setter_Getter.SetgetVodSubcategory;
import israel13.androidtv.Utils.Constant;
import israel13.androidtv.Utils.CustomGridView;
import israel13.androidtv.Utils.CustomRecyclerView;
import israel13.androidtv.Utils.NetworkUtil;
import israel13.androidtv.Utils.RtlGridLayoutManager;

/**
 * Created by puspak on 10/7/17.
 */

public class VodSub_Subcategory_Fragment extends Fragment implements IJSONParseListener {

    //public static CustomRecyclerView gridview_vod_subcategory;
    public static CustomGridView gridview_vod_subcategory;
    public static TextView cat_name;
    View tempView = null;
    int GET_VOD_SUBCATEGORIES=170;
    int GET_VOD_SUB_SUBCATEGORYLIST_PAGE = 171;
    ProgressDialog pDialog;

    //public static ArrayList<SetgetVodSubcategory> subcategoriesList;
    ArrayList<SetgetVodSubcategory> listGridViewCategories;
    final int PAGE_ROW_COUNT = 8;

    VodSubCategoryGridRecyclerAdapter gridAdapter;
    private int nSelectedIndex = 0;
    private int nSelectedTop = 0;
    private int nTotalPageCnt = 0;
    private int nCurPageNo = 0;
    private boolean bPageLoading = false;
    private boolean bLoadToEnd = false;
    private int nRequestCount = 0;

    public static String vod_category_id="",Cat_name="";
    SharedPreferences logindetails;

    private View mSubCategoryBeforeView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        listGridViewCategories = new ArrayList<SetgetVodSubcategory>();
        gridAdapter = new VodSubCategoryGridRecyclerAdapter(listGridViewCategories,getActivity());
        logindetails = getActivity().getSharedPreferences("logindetails", LoginActivity.MODE_PRIVATE);
        Call_api();
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_vod_sub_subcategory, null);


        //gridview_vod_subcategory = (CustomRecyclerView) layout.findViewById(R.id.gridview_vod_subcategory);
        gridview_vod_subcategory = (CustomGridView) layout.findViewById(R.id.gridview_vod_sub_subcategory);
        //gridview_vod_subcategory.setItemViewCacheSize(500);

        cat_name = (TextView)layout.findViewById(R.id.cat_name);
        cat_name.setText(Cat_name);

        gridview_vod_subcategory.setAdapter(gridAdapter);

        gridview_vod_subcategory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0) return;
                if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                    //Log.e("...", "Last Item Wow !");
                    //Do pagination.. i.e. fetch new data
                    if (!bPageLoading ) {
                        if (!bLoadToEnd) {
                            bPageLoading = true;
                            GetSubSubcategoryListPage(nCurPageNo + 1);
                        }
                    }
                }
            }
        });

        gridview_vod_subcategory.setSelectedIndex(nSelectedIndex);
        gridview_vod_subcategory.setSelectedTop(nSelectedTop);

        gridview_vod_subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (tempView != null){

                    tempView.animate().scaleX(1).scaleY(1).setDuration(200);
                }
                view.animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
                tempView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gridview_vod_subcategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (tempView != null)
                        tempView.animate().scaleX(1).scaleY(1).setDuration(200);
                    tempView = null;
                }else{
                    if (gridview_vod_subcategory.getSelectedView() != null) {
                        gridview_vod_subcategory.getSelectedView().animate().scaleX(1.08f).scaleY(1.08f).setDuration(200);
                        tempView = gridview_vod_subcategory.getSelectedView();
                    }
                }

            }
        });
        gridview_vod_subcategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //HomeActivity home = (HomeActivity)getActivity();
                //if(home != null){
                //    home.setSelectedPosition(HomeActivity.VodSub_SubCategoryGridRecycler, position);
                //}
                nSelectedIndex = position;
                nSelectedTop = view.getTop();
                if (listGridViewCategories.get(position).getGotoact().equalsIgnoreCase("0")) {

                    VodSub_Sub_Subcategory_Fragment.vod_category_id = listGridViewCategories.get(position).getSubcat_id();
                    VodSub_Subcategory_Fragment.cat_name.setText(VodSubcategory_Fragment.Cat_name+" > "+listGridViewCategories.get(position).getName());
                    VodSub_Sub_Subcategory_Fragment.Cat_name=VodSub_Subcategory_Fragment.Cat_name+" > "+listGridViewCategories.get(position).getName();

                    VodSub_Sub_Subcategory_Fragment Vod_sub_sub_sub_cat_frag= new VodSub_Sub_Subcategory_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.add(R.id.fram_container, Vod_sub_sub_sub_cat_frag, "layout_sub_sub_subcat");
                    ft1.addToBackStack(null);
                    ft1.commit();

                } else if (listGridViewCategories.get(position).getGotoact().equalsIgnoreCase("2")) {

                    VodSubcategory_Fragment.vod_subcategory_id = listGridViewCategories.get(position).getSubcat_id();
                    VodSubcategory_Fragment.vod_subcategory_image = listGridViewCategories.get(position).getShowpic();
                    VodSubcategory_Fragment.vod_subcategory_name = listGridViewCategories.get(position).getName();
                    VodSubcategory_Fragment.vod_subcategory_year = listGridViewCategories.get(position).getYear();
                    if (VodSubcategory_Fragment.position > -1 && VodSubcategory_Fragment.position < listGridViewCategories.size()){
                        listGridViewCategories.get(VodSubcategory_Fragment.position).setIsinfav(VodSubcategory_Fragment.is_infav);
                    }
                    VodSubcategory_Fragment.position = position;
                    VodSubcategory_Fragment.is_infav =listGridViewCategories.get(position).getIsinfav();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.cat_name.getText().toString();
                    //Cat_name=Cat_name+" | "+subcategoriesList.get(position).getName();

                    VodTvShowVideo_Fragment allvod_tv_show_cat_frag= new VodTvShowVideo_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.add(R.id.fram_container, allvod_tv_show_cat_frag, "layout_tvshow_screen");
                    //ft1.add(R.id.fram_container, allvod_tv_show_cat_frag);
                    ft1.addToBackStack(null);
                    ft1.commit();

                } else {
                    VodSubcategory_Fragment.vod_subcategory_id = listGridViewCategories.get(position).getSubcat_id();
                    VodSubcategory_Fragment.vod_subcategory_image = listGridViewCategories.get(position).getShowpic();
                    VodSubcategory_Fragment.vod_subcategory_name = listGridViewCategories.get(position).getName();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.cat_name.getText().toString();
                    VodSubcategory_Fragment.Cat_name=VodSubcategory_Fragment.Cat_name+" > "+listGridViewCategories.get(position).getName();
                    VodMovieVideoPlayActivity.movie_play_cat=listGridViewCategories.get(position).getName();
                    VodSubcategoryMovies_Fragment allvod_sub_movie_cat_frag= new VodSubcategoryMovies_Fragment();
                    FragmentTransaction ft1 =((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                    // ft1.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    ft1.add(R.id.fram_container, allvod_sub_movie_cat_frag, "layout_VodMoviesubcategory");
                    ft1.addToBackStack(null);
                    ft1.commit();
                }
            }
        });

        layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    gridview_vod_subcategory.requestFocus();
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        getView().setFocusableInTouchMode(false);
        getView().requestFocus();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ((HomeActivity)getActivity()).setSelectedPosition(HomeActivity.VodSub_SubCategoryGridRecycler, 0);
        super.onDestroy();
    }

    public void Call_api()
    {
        if (NetworkUtil.checkNetworkAvailable(getActivity())) {
            GetSubSubcategoryListPage(1);
        } else {
            ShowErrorAlert(getActivity(),"Please check your network connection..");
        }
    }

    public void ShowErrorAlert(final Context c, String text)
    {
        android.app.AlertDialog.Builder alertDialogBuilder;
        ContextThemeWrapper ctx = new ContextThemeWrapper(c, R.style.Theme_Sphinx);
        alertDialogBuilder = new  android.app.AlertDialog.Builder(ctx);

        alertDialogBuilder.setTitle("Alert!");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call_api();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
    void GetAllSubcategories() {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        //parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("id",vod_category_id);
        MyVolley.init(getActivity());
        ShowProgressDilog(getActivity());
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatesubs.php", GET_VOD_SUBCATEGORIES, this, parms, false);
    }

    void GetSubSubcategoryListPage(int page) {
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle parms = new Bundle();
        parms.putString("sid", logindetails.getString("sid", ""));
        parms.putString("id", vod_category_id);
        parms.putString("page", String.valueOf(page));
        parms.putString("pagesize", String.valueOf((gridview_vod_subcategory==null?5:gridview_vod_subcategory.nRowCellCount) * PAGE_ROW_COUNT));
        MyVolley.init(getActivity());
        ShowProgressDilog(getActivity());
        mResponse.getResponse(Request.Method.GET, Constant.BASE_URL + "vodcatesubs.php", GET_VOD_SUB_SUBCATEGORYLIST_PAGE, this, parms, false);
        nRequestCount = (gridview_vod_subcategory==null?5:gridview_vod_subcategory.nRowCellCount) * PAGE_ROW_COUNT;
    }

        @Override
    public void ErrorResponse(VolleyError error, int requestCode) {
            DismissProgress(getActivity());
            Constant.ShowErrorToast(getActivity());
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        DismissProgress(getActivity());
        if (requestCode == GET_VOD_SUB_SUBCATEGORYLIST_PAGE) {
            System.out.println("Response for Vod subcategory list `--------------------" + response.toString());
            boolean dataLoad = false;

            try {

                JSONObject results = response.getJSONObject("results");
                JSONObject subetcs = results.getJSONObject("subetcs");

                if (nTotalPageCnt == 0) nTotalPageCnt = Constant.parseInt(subetcs.optString("totp"));

                JSONArray sons = subetcs.getJSONArray("sons");
                if (sons.length() > 0) {
                    dataLoad = true;
                    nCurPageNo++;
                }
                if (sons.length() <= 0 || sons.length() < nRequestCount) bLoadToEnd = true;

                for (int i=0 ; i < sons.length() ; i ++)
                {
                    try {
                        JSONObject object = sons.getJSONObject(i);

                        SetgetVodSubcategory subcategory = new SetgetVodSubcategory();
                        subcategory.setSubcat_id(object.getString("id"));
                        subcategory.setName(object.getString("name"));
                        subcategory.setYear(object.getString("year"));
                        subcategory.setViews(object.getString("views"));
                        subcategory.setStars(object.getString("stars"));
                        // subcategory.setGenre(object.getString("genre"));
                        //subcategory.setDescription(object.getString("description"));
                        subcategory.setShowpic(object.getString("showpic"));
                        subcategory.setGotoact(object.getString("gotoact"));
                        subcategory.setIsepisode(object.getString("isepisode"));
                        subcategory.setIsinfav(object.optString("isinfav", "0"));
                        listGridViewCategories.add(subcategory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            //gridview_vod_subcategory.setAdapter(new VodSub_SubCategoryGridRecyclerAdapter(subcategoriesList,getActivity()));
            if (dataLoad) {
                gridAdapter.notifyDataSetChanged();
                if (nCurPageNo == 1) {
                    gridview_vod_subcategory.setSelection(nSelectedIndex);
                }
            }
            gridview_vod_subcategory.requestFocus();
            bPageLoading = false;
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }
    
    /*private boolean loadPageData() {
        int loadCount = gridview_vod_subcategory.getNumColumns() * PAGE_ROW_COUNT;
        int origCnt = listGridViewCategories.size();
        if (subcategoriesList.size() > 0 && subcategoriesList.size() > listGridViewCategories.size()) {
            int start = listGridViewCategories.size();
            int end = start + loadCount;
            if (end > subcategoriesList.size()) end = subcategoriesList.size();
            listGridViewCategories.addAll(subcategoriesList.subList(start, end));
        }
        return !(origCnt == listGridViewCategories.size());
    }*/

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    void ShowProgressDilog(Context c) {
        pDialog = new ProgressDialog(c);
        pDialog.show();
        pDialog.setCancelable(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.setContentView(R.layout.layout_progress_dilog);
    }

    void DismissProgress(Context c) {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    public void onFragmentTopFromBackState() {
        View v = getView();
        if (v != null) {
            ((LinearLayout) (v.findViewById(R.id.layout_root_vodsubusb))).setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            v.setFocusableInTouchMode(false);
            v.requestFocus();
        }
    }

    public void onFragmentBackState() {
        View v = getView();
        if (v != null)
            ((LinearLayout)(v.findViewById(R.id.layout_root_vodsubusb))).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }
}
