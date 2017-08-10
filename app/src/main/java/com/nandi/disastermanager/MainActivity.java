package com.nandi.disastermanager;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.ElevationSource;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.DefaultSceneViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nandi.disastermanager.adapter.RcPersonAdapter;
import com.nandi.disastermanager.entity.DisasterDetailInfo;
import com.nandi.disastermanager.entity.DisasterInfo;
import com.nandi.disastermanager.entity.DisasterPoint;
import com.nandi.disastermanager.entity.PersonInfo;
import com.nandi.disastermanager.entity.PersonLocation;
import com.nandi.disastermanager.ui.MyRadioGroup;
import com.nandi.disastermanager.ui.WaitingDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.iv_area_back)
    ImageView ivAreaBack;
    @BindView(R.id.ll_area)
    LinearLayout llArea;
    @BindView(R.id.iv_data_back)
    ImageView ivDataBack;
    @BindView(R.id.iv_dangerpoint_more)
    ImageView ivDangerpointMore;
    @BindView(R.id.rg_rainfall)
    RadioGroup rgRainfall;
    @BindView(R.id.iv_staff_more)
    ImageView ivStaffMore;
    @BindView(R.id.rg_dangerpoint)
    RadioGroup rgDangerpoint;
    @BindView(R.id.iv_equipment_more)
    ImageView ivEquipmentMore;
    @BindView(R.id.rg_staff)
    RadioGroup rgStaff;
    @BindView(R.id.iv_rainfall_more)
    ImageView ivRainfallMore;
    @BindView(R.id.rg_equipment)
    RadioGroup rgEquipment;
    @BindView(R.id.ll_data)
    LinearLayout llData;
    @BindView(R.id.ll_dangerpoint)
    LinearLayout llDangerpoint;
    @BindView(R.id.ll_staff)
    LinearLayout llStaff;
    @BindView(R.id.ll_equipment)
    LinearLayout llEquipment;
    @BindView(R.id.ll_rainfall)
    LinearLayout llRainfall;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.sceneView)
    SceneView sceneView;
    @BindView(R.id.rb_ssyl)
    RadioButton rbSsyl;
    @BindView(R.id.rb_yldzx)
    RadioButton rbYldzx;
    @BindView(R.id.rb_disaster_point)
    RadioButton rbDisasterPoint;
    @BindView(R.id.rb_canceled_point)
    RadioButton rbCanceledPoint;//已消耗
    @BindView(R.id.rb_handled_point)
    RadioButton rbHandledPoint;//未消耗
    @BindView(R.id.tv_disaster_number)
    TextView tvDisasterNumber;
    @BindView(R.id.tv_person_number)
    TextView tvPersonNumber;
    @BindView(R.id.tv_equipment_number)
    TextView tvEquipmentNumber;
    @BindView(R.id.tv_leader_number)
    TextView tvLeaderNumber;
    @BindView(R.id.tv_charge_number)
    TextView tvChargeNumber;
    @BindView(R.id.rb_qcqf_person)
    RadioButton rbQcqfPerson;
    @BindView(R.id.rb_zs_person)
    RadioButton rbZsPerson;
    @BindView(R.id.rb_pq_person)
    RadioButton rbPqPerson;
    @BindView(R.id.rb_dhz_person)
    RadioButton rbDhzPerson;
    @BindView(R.id.iv_qxsy)
    ImageView ivQxsy;
    @BindView(R.id.rb_jinqiao)
    RadioButton rbJinqiao;
    @BindView(R.id.rb_shilin)
    RadioButton rbShilin;
    @BindView(R.id.rg_qxsy)
    RadioGroup rgQxsy;
    @BindView(R.id.ll_qxsy)
    LinearLayout llQxsy;
    @BindView(R.id.rb_equipment_jiance)
    RadioButton rbEquipmentJiance;
    @BindView(R.id.rb_equipment_laba)
    RadioButton rbEquipmentLaba;
    @BindView(R.id.rb_equipment_yingji)
    RadioButton rbEquipmentYingji;
    @BindView(R.id.rb_equipment_fengsu)
    RadioButton rbEquipmentFengsu;
    private boolean llAreaState = false;
    private boolean llDataState = false;
    private int llMoreState = -1;
    private int llMoreStateBefore = -1;
    private View view;

    ArcGISMapImageLayer lowImageLayer;
    ArcGISMapImageLayer highImageLayer;
    ArcGISMapImageLayer vectorLayer;
    ArcGISMapImageLayer dengZXLayer;
    ArcGISMapImageLayer dianziLayer;
    ArcGISMapImageLayer ssYLLayer;
    ArcGISSceneLayer jinQiaoLayer;
    ArcGISSceneLayer shiLinLayer;
    private ArcGISScene scene;
    private ArcGISTiledElevationSource elevationSource;
    private LayerList layers;
    private Surface.ElevationSourceList elevationSources;
    private List<DisasterPoint> disasterPoints;
    private List<PersonLocation> qcPersons = new ArrayList<>();
    private List<PersonLocation> zsPersons = new ArrayList<>();
    private List<PersonLocation> pqPersons = new ArrayList<>();
    private List<PersonLocation> dhzPersons = new ArrayList<>();
    private GraphicsOverlay graphicsOverlay;
    private GraphicsOverlay personGraphicsOverlay;
    private GraphicsOverlay localGraphicsOverlay;
    private GraphicsOverlay equipmentGraphicOverlay;
    private ListenableList<GraphicsOverlay> graphicsOverlays;
    private List<Graphic> allGraphics = new ArrayList<>();//所有的灾害点图标
    private List<Graphic> consumedGraphics = new ArrayList<>();//已消耗灾害点图标
    private List<Graphic> notConsumeGraphics = new ArrayList<>();//未消耗灾害点图标
    private List<Graphic> allHuaPOGraphics = new ArrayList<>();
    private List<Graphic> allNiSHILiuGraphics = new ArrayList<>();
    private List<Graphic> allWeiYanGraphics = new ArrayList<>();
    private List<Graphic> allXiePoGraphics = new ArrayList<>();
    private List<Graphic> allTanTaGraphics = new ArrayList<>();
    private List<Graphic> allLieFengGraphics = new ArrayList<>();
    private List<Graphic> allTaAnGraphics = new ArrayList<>();

    private List<Graphic> notHuaPOGraphics = new ArrayList<>();
    private List<Graphic> notNiSHILiuGraphics = new ArrayList<>();
    private List<Graphic> notWeiYanGraphics = new ArrayList<>();
    private List<Graphic> notXiePoGraphics = new ArrayList<>();
    private List<Graphic> notTanTaGraphics = new ArrayList<>();
    private List<Graphic> notLieFengGraphics = new ArrayList<>();
    private List<Graphic> notTaAnGraphics = new ArrayList<>();

    private List<Graphic> conHuaPOGraphics = new ArrayList<>();
    private List<Graphic> conNiSHILiuGraphics = new ArrayList<>();
    private List<Graphic> conWeiYanGraphics = new ArrayList<>();
    private List<Graphic> conXiePoGraphics = new ArrayList<>();
    private List<Graphic> conTanTaGraphics = new ArrayList<>();
    private List<Graphic> conLieFengGraphics = new ArrayList<>();
    private List<Graphic> conTaAnGraphics = new ArrayList<>();
    private List<Graphic> qcGraphics = new ArrayList<>();
    private List<Graphic> zsGraphics = new ArrayList<>();
    private List<Graphic> pqGraphics = new ArrayList<>();
    private List<Graphic> dhzGraphics = new ArrayList<>();
    private List<Graphic> jianceGraphics = new ArrayList<>();
    private ListenableList<Graphic> graphics;
    private ListenableList<Graphic> personGraphics;
    private ListenableList<Graphic> localGraphics;
    private ListenableList<Graphic> equipmentGraphics;
    private RadioGroup rg;
    private Dialog waitingDialog;
    private DisasterDetailInfo disasterDetailInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initStaData();
        initLocalData();
        dianziLayer = new ArcGISMapImageLayer(getResources().getString(R.string.dianziditu_url));
        lowImageLayer = new ArcGISMapImageLayer(getResources().getString(R.string.image_layer_13_url));
        highImageLayer = new ArcGISMapImageLayer(getResources().getString(R.string.image_layer_13_19_url));
        vectorLayer = new ArcGISMapImageLayer(getResources().getString(R.string.shiliangtu_url));
        dengZXLayer = new ArcGISMapImageLayer(getResources().getString(R.string.yuliang_url));
        ssYLLayer = new ArcGISMapImageLayer(getResources().getString(R.string.ssyl_url));
        jinQiaoLayer = new ArcGISSceneLayer(getResources().getString(R.string.jinqiao_qxsy_url));
        shiLinLayer = new ArcGISSceneLayer(getResources().getString(R.string.shilin_qxsy_url));
        elevationSource = new ArcGISTiledElevationSource(
                getResources().getString(R.string.elevation_image_service));
        scene = new ArcGISScene();
        layers = scene.getOperationalLayers();
        graphicsOverlay = new GraphicsOverlay();
        personGraphicsOverlay = new GraphicsOverlay();
        localGraphicsOverlay = new GraphicsOverlay();
        equipmentGraphicOverlay=new GraphicsOverlay();
        graphics = graphicsOverlay.getGraphics();
        personGraphics = personGraphicsOverlay.getGraphics();
        localGraphics = localGraphicsOverlay.getGraphics();
        equipmentGraphics=equipmentGraphicOverlay.getGraphics();
        graphicsOverlays = sceneView.getGraphicsOverlays();
        elevationSources = scene.getBaseSurface().getElevationSources();
        scene.setBasemap(Basemap.createImagery());
        sceneView.setScene(scene);
        setListeners();
    }

    private void initLocalData() {
        pqPersons.add(new PersonLocation("28.95514", "106.926366", 10011));
        pqPersons.add(new PersonLocation("28.816001", "106.816087", 10021));
        pqPersons.add(new PersonLocation("28.943602", "106.882525", 10031));
        pqPersons.add(new PersonLocation("29.031026", "106.965438", 10041));
        pqPersons.add(new PersonLocation("28.869188", "106.908478", 10051));
        pqPersons.add(new PersonLocation("28.940619", "106.945398", 10061));
        pqPersons.add(new PersonLocation("28.861747", "106.842987", 10071));
        pqPersons.add(new PersonLocation("28.914455", "107.003855", 10081));
        pqPersons.add(new PersonLocation("29.070643", "106.880268", 10091));
        pqPersons.add(new PersonLocation("28.95102", "106.923961", 10101));

        dhzPersons.add(new PersonLocation("28.96346", "106.927437", 293));
        dhzPersons.add(new PersonLocation("28.96446", "106.928437", 303));
        dhzPersons.add(new PersonLocation("28.96546", "106.929437", 313));
        dhzPersons.add(new PersonLocation("28.96646", "106.927437", 333));

        zsPersons.add(new PersonLocation("28.961367", "106.932906", 10012));
        zsPersons.add(new PersonLocation("28.948905", "106.888573", 10022));
        zsPersons.add(new PersonLocation("29.07643", "106.886891", 10032));
        zsPersons.add(new PersonLocation("28.9435", "106.955571", 10042));

        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.person);
        final PictureMarkerSymbol symbol = new PictureMarkerSymbol(drawable);
        symbol.setWidth(30);
        symbol.setHeight(30);
        symbol.setOffsetY(11);
        symbol.loadAsync();
        symbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                for (PersonLocation disasterPoint : pqPersons) {
                    Point point = new Point(Double.valueOf(disasterPoint.getLon()), Double.valueOf(disasterPoint.getLat()), SpatialReferences.getWgs84());
                    Graphic graphic = new Graphic(point, symbol);
                    graphic.setZIndex(disasterPoint.getId());
                    pqGraphics.add(graphic);
                }
                for (PersonLocation disasterPoint : zsPersons) {
                    Point point = new Point(Double.valueOf(disasterPoint.getLon()), Double.valueOf(disasterPoint.getLat()), SpatialReferences.getWgs84());
                    Graphic graphic = new Graphic(point, symbol);
                    graphic.setZIndex(disasterPoint.getId());
                    zsGraphics.add(graphic);
                }
                for (PersonLocation disasterPoint : dhzPersons) {
                    Point point = new Point(Double.valueOf(disasterPoint.getLon()), Double.valueOf(disasterPoint.getLat()), SpatialReferences.getWgs84());
                    Graphic graphic = new Graphic(point, symbol);
                    graphic.setZIndex(disasterPoint.getId());
                    dhzGraphics.add(graphic);
                }

            }

        });
    }


    private void initStaData() {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.statistics_url))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Toast.makeText(getApplicationContext(), "统计信息获取失败，请检查网路！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject oj = object.getJSONObject("data");
                            tvDisasterNumber.setText(oj.getString("disasterTotal"));
                            tvPersonNumber.setText(oj.getString("personnelTotal"));
                            tvChargeNumber.setText(oj.getString("LandTotal"));
                            tvEquipmentNumber.setText(oj.getString("equipmentTotal"));
                            tvLeaderNumber.setText(oj.getString("townshipTotal"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setListeners() {
        ivAreaBack.setOnClickListener(this);
        ivDataBack.setOnClickListener(this);
        llRainfall.setOnClickListener(this);
        llDangerpoint.setOnClickListener(this);
        llStaff.setOnClickListener(this);
        llEquipment.setOnClickListener(this);
        llQxsy.setOnClickListener(this);
        rbSsyl.setOnCheckedChangeListener(this);
        rbYldzx.setOnCheckedChangeListener(this);
        rbDisasterPoint.setOnCheckedChangeListener(this);
        rbCanceledPoint.setOnCheckedChangeListener(this);
        rbHandledPoint.setOnCheckedChangeListener(this);
        rbQcqfPerson.setOnCheckedChangeListener(this);
        rbDhzPerson.setOnCheckedChangeListener(this);
        rbZsPerson.setOnCheckedChangeListener(this);
        rbPqPerson.setOnCheckedChangeListener(this);
        rbJinqiao.setOnCheckedChangeListener(this);
        rbShilin.setOnCheckedChangeListener(this);
        rbEquipmentFengsu.setOnCheckedChangeListener(this);
        rbEquipmentJiance.setOnCheckedChangeListener(this);
        rbEquipmentLaba.setOnCheckedChangeListener(this);
        rbEquipmentYingji.setOnCheckedChangeListener(this);
        sceneView.setOnTouchListener(new DefaultSceneViewOnTouchListener(sceneView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // get the screen point where user tapped
                android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
                // identify graphics on the graphics overlay
                final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = sceneView.identifyGraphicsOverlayAsync(graphicsOverlay, screenPoint, 10.0, false, 2);
                final ListenableFuture<IdentifyGraphicsOverlayResult> personIdentifyGraphic = sceneView.identifyGraphicsOverlayAsync(personGraphicsOverlay, screenPoint, 10.0, false, 2);
                final ListenableFuture<IdentifyGraphicsOverlayResult> localIdentifyGraphic = sceneView.identifyGraphicsOverlayAsync(localGraphicsOverlay, screenPoint, 10.0, false, 2);
                final ListenableFuture<IdentifyGraphicsOverlayResult> equipmentIdentifyGraphic  = sceneView.identifyGraphicsOverlayAsync(equipmentGraphicOverlay, screenPoint, 10.0, false, 2);
                identifyGraphic.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = identifyGraphic.get();
                            if (identifyGraphicsOverlayResult.getGraphics().size() > 0) {
                                int zIndex = identifyGraphicsOverlayResult.getGraphics().get(0).getZIndex();
                                showInfo(zIndex);
                            }
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                personIdentifyGraphic.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = personIdentifyGraphic.get();
                            if (identifyGraphicsOverlayResult.getGraphics().size() > 0) {
                                int zIndex = identifyGraphicsOverlayResult.getGraphics().get(0).getZIndex();
                                showPersonInfo(zIndex);
                            }
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                localIdentifyGraphic.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = localIdentifyGraphic.get();
                            if (identifyGraphicsOverlayResult.getGraphics().size() > 0) {
                                int zIndex = identifyGraphicsOverlayResult.getGraphics().get(0).getZIndex();
                                showLocalPersonInfo(zIndex);
                            }
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                equipmentIdentifyGraphic.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdentifyGraphicsOverlayResult identifyGraphicsOverlayResult = equipmentIdentifyGraphic.get();
                            if (identifyGraphicsOverlayResult.getGraphics().size() > 0) {
                                int zIndex = identifyGraphicsOverlayResult.getGraphics().get(0).getZIndex();
                                showEquipmentInfo(zIndex);
                            }
                        } catch (InterruptedException | ExecutionException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                return super.onSingleTapConfirmed(e);
            }
        });

    }

    private void showEquipmentInfo(int zIndex) {
        //TODO 根据Id显示设备信息
    }

    private void showLocalPersonInfo(int zIndex) {
        final String id, type;
        String s = String.valueOf(zIndex);
        id = s.substring(0, s.length() - 1);
        type = String.valueOf(s.charAt(s.length() - 1));
        Log.d(TAG, "id:" + id + "\ntype:" + type);
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.get_local_person_info))
                .addParams("id", id)
                .addParams("type", type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Log.d("limeng", "response:" + response);
                        Log.d("limeng", "response:" + response);
                        String online = null;
                        String dispicture = null;
                        TextView tvOnline = null;
                        try {
                            JSONObject object = new JSONObject(response);
                            String info = "";
                            View view = null;
                            if ("1".equals(type)) {
                                String name = object.getString("admin_name");
                                String address = object.getString("area_location");
                                String mobile = object.getString("real_mobile");
                                online = object.getString("online");
                                dispicture = object.getString("admin_pic");
                                info = "姓名：" + name + "\n"
                                        + "乡镇：" + address + "\n"
                                        + "电话：" + mobile;
                                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_person_info1, null);
                                ((TextView) view.findViewById(R.id.tv_1_person_name)).append(name == null ? "" : name);
                                ((TextView) view.findViewById(R.id.tv_1_person_address)).append(address == null ? "" : address);
                                ((TextView) view.findViewById(R.id.tv_1_person_mobile)).append(mobile == null ? "" : mobile);
                                tvOnline = ((TextView) view.findViewById(R.id.tv_1_person_is));
                            } else if ("3".equals(type)) {
                                String name = object.getString("name");
                                String address = object.getString("location");
                                String tel = object.getString("zhibantel");
                                String job = object.getString("job");
                                String mobile = object.getString("iphone");
                                online = object.getString("online");
                                dispicture = object.getString("url");
                                info = "姓名：" + name + "\n"
                                        + "地址：" + address + "\n"
                                        + "值班电话：" + tel + "\n"
                                        + "职位：" + job + "\n"
                                        + "手机：" + mobile;
                                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_person_info2, null);
                                ((TextView) view.findViewById(R.id.tv_2_person_name)).append(name == null ? "" : name);
                                ((TextView) view.findViewById(R.id.tv_2_person_address)).append(address == null ? "" : address);
                                ((TextView) view.findViewById(R.id.tv_2_person_mobile)).append(mobile == null ? "" : mobile);
                                ((TextView) view.findViewById(R.id.tv_2_person_tel)).append(tel == null ? "" : tel);
                                ((TextView) view.findViewById(R.id.tv_2_person_job)).append(job == null ? "" : job);
                                tvOnline = ((TextView) view.findViewById(R.id.tv_2_person_is));
                            } else if ("2".equals(type)) {
                                String name = object.getString("disname");
                                String gender = object.getString("gender");
                                String age = object.getString("age");
                                String mobile = object.getString("phone");
                                String manage_area = object.getString("manage_area");
                                String address = object.getString("disarea");
                                String danwei = object.getString("unit_name");
                                online = object.getString("online");
                                String Head_url = object.getString("unit_name");
                                dispicture = object.getString("dispicture");
                                info = "姓名：" + name + "\n"
                                        + "性别：" + gender + "\n"
                                        + "年龄：" + age + "\n"
                                        + "电话：" + mobile + "\n"
                                        + "地址：" + address + "\n"
                                        + "单位：" + danwei + "\n"
                                        + "管理区域：" + manage_area + "\n";
                                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_person_info3, null);
                                ((TextView) view.findViewById(R.id.tv_3_person_name)).append(name == null ? "" : name);
                                ((TextView) view.findViewById(R.id.tv_3_person_gender)).append(gender == null ? "" : gender);
                                ((TextView) view.findViewById(R.id.tv_3_person_age)).append(age == null ? "" : age);
                                ((TextView) view.findViewById(R.id.tv_3_person_mobile)).append(mobile == null ? "" : mobile);
                                ((TextView) view.findViewById(R.id.tv_3_person_address)).append(address == null ? "" : address);
                                ((TextView) view.findViewById(R.id.tv_3_person_danwei)).append(danwei == null ? "" : danwei);
                                ((TextView) view.findViewById(R.id.tv_3_person_manage_area)).append(manage_area == null ? "" : manage_area);
                                tvOnline = ((TextView) view.findViewById(R.id.tv_3_person_is));
                            }
                            if ("0".equals(online)) {
                                tvOnline.append("不在线");
                                tvOnline.setTextColor(Color.RED);
                            } else {
                                tvOnline.append("在线");
                                tvOnline.setTextColor(Color.GREEN);
                            }
                            new AlertDialog.Builder(MainActivity.this)
                                    .setView(view)
                                    .show();
                            Log.d("limeng", "dispicture=" + dispicture);
                            Glide.with(MainActivity.this)
                                    .load("http://183.230.108.112:9077/cqapp/" + dispicture)
                                    .placeholder(R.mipmap.downloading)
                                    .thumbnail(0.1f)
                                    .error(R.mipmap.download_pass)
                                    .into((ImageView) view.findViewById(R.id.dialog_image));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showPersonInfo(int zIndex) {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.get_person_info))
                .addParams("id", zIndex + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<PersonInfo>>() {
                        }.getType();
                        List<PersonInfo> personInfos = gson.fromJson(response, type);
                        PersonInfo personInfo = personInfos.get(0);
                        Log.d(TAG, "人员信息：" + personInfo);
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_person_info, null);
                        ((TextView) view.findViewById(R.id.tv_0_person_name)).append(personInfo.getName() == null ? "" : personInfo.getName());
                        ((TextView) view.findViewById(R.id.tv_0_person_work)).append(personInfo.getWork() == null ? "" : personInfo.getWork());
                        String polics = "";
                        switch (personInfo.getPolics() == null ? -1 : Integer.parseInt(personInfo.getPolics())) {
                            case 1:
                                polics = "中共党员";
                                break;
                            case 2:
                                polics = "中共预备党员";
                                break;
                            case 3:
                                polics = "共青团员";
                                break;
                            case 4:
                                polics = "群众";
                                break;
                            case 5:
                                polics = "民革党员";
                                break;
                            case 6:
                                polics = "民盟盟员";
                                break;
                            case 7:
                                polics = "民建会员";
                                break;
                        }
                        ((TextView) view.findViewById(R.id.tv_0_person_polics)).append(polics);
                        ((TextView) view.findViewById(R.id.tv_0_person_nation)).append(personInfo.getNation() == null ? "" : personInfo.getNation());
                        ((TextView) view.findViewById(R.id.tv_0_person_address)).append(personInfo.getAddress() == null ? "" : personInfo.getAddress());
                        TextView tvOnline = ((TextView) view.findViewById(R.id.tv_0_person_is));
                        if (personInfo.getOnline() == 0) {
                            tvOnline.append("不在线");
                            tvOnline.setTextColor(Color.RED);
                        } else {
                            tvOnline.append("在线");
                            tvOnline.setTextColor(Color.GREEN);
                        }
                        ((TextView) view.findViewById(R.id.tv_0_person_ismonitor)).append(personInfo.getIs_monitor() == 1 ? "监测负责人" : "监测人");
                        ((TextView) view.findViewById(R.id.tv_0_person_brithday)).append(personInfo.getBrithday() == null ? "" : personInfo.getBrithday());
                        ((TextView) view.findViewById(R.id.tv_0_person_realmobile)).append(personInfo.getReal_mobile() == null ? "" : personInfo.getReal_mobile());
                        ((TextView) view.findViewById(R.id.tv_0_person_mobile)).append(personInfo.getMobile() == null ? "" : personInfo.getMobile());
                        new AlertDialog.Builder(MainActivity.this)
                                .setView(view)
                                .show();
                        Glide.with(MainActivity.this)
                                .load("http://183.230.108.112:9077/cqapp/" + personInfo.getHead_url())
                                .placeholder(R.mipmap.downloading)
                                .thumbnail(0.1f)
                                .error(R.mipmap.download_pass)
                                .into((ImageView) view.findViewById(R.id.dialog_image));
                    }
                });
    }

    private void showInfo(int zIndex) {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.get_disaster_info))
                .addParams("id", zIndex + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<DisasterInfo>>() {
                        }.getType();
                        List<DisasterInfo> disasterInfos = gson.fromJson(response, type);
                        DisasterInfo disasterInfo = disasterInfos.get(0);

                        setDialogViewDatas(disasterInfo);
                        WaitingDialog.closeDialog(waitingDialog);
                    }
                });
    }


    private void setDialogViewDatas(final DisasterInfo pointInfo) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_disasterinfo, null);
        final LinearLayout llBaseInfo = (LinearLayout) view.findViewById(R.id.ll_base_info);
        rg = (RadioGroup) view.findViewById(R.id.rg_disaster_info);
        final List<String> mList = new ArrayList<>();
        mList.add(R.mipmap.t5001101000840101_1 + "");
        mList.add(R.mipmap.t5001101000840101_2 + "");
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_disaster_info);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_base_info:
                        llBaseInfo.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rbtn_check_detail:
                        llBaseInfo.setVisibility(View.VISIBLE);
                        setOkhttpDetails(pointInfo.getId() + "");
                        break;
                }
            }

        });


        TextView tvDisName = (TextView) view.findViewById(R.id.tv_dis_name);
        TextView tvDisType = (TextView) view.findViewById(R.id.tv_dis_type);
        TextView tvDisNo = (TextView) view.findViewById(R.id.tv_dis_no);
        TextView tvAreaId = (TextView) view.findViewById(R.id.tv_area_id);
        TextView tvDisLocation = (TextView) view.findViewById(R.id.tv_dis_location);
        TextView tvDisCause = (TextView) view.findViewById(R.id.tv_dis_cause);
        TextView tvImperilMan = (TextView) view.findViewById(R.id.tv_imperil_man);
        TextView tvImperilFamilies = (TextView) view.findViewById(R.id.tv_imperil_families);
        TextView tvMainObject = (TextView) view.findViewById(R.id.tv_main_object);

        tvDisName.setText(pointInfo.getDis_name() == null ? "" : pointInfo.getDis_name());
        tvDisType.setText(pointInfo.getDis_type() + "");
        tvDisNo.setText(pointInfo.getDis_no() == null ? "" : pointInfo.getDis_no());
        tvAreaId.setText(pointInfo.getDis_location() == null ? "" : pointInfo.getDis_location() + "");
        tvDisLocation.setText(pointInfo.getDis_location() == null ? "" : pointInfo.getDis_location() + "");
        tvDisCause.setText(pointInfo.getDis_cause() == null ? "" : pointInfo.getDis_cause() + "");
        tvImperilMan.setText(pointInfo.getImperil_man() + "");
        tvImperilFamilies.setText(pointInfo.getImperil_families() + "");
        tvMainObject.setText(pointInfo.getMain_object() == null ? "" : pointInfo.getMain_object() + "");
        tvDisName.setText(pointInfo.getDis_name() == null ? "" : pointInfo.getDis_name());
        tvDisType.setText(pointInfo.getDis_type() + "");
        tvDisLocation.setText(pointInfo.getDis_location() == null ? "" : pointInfo.getDis_location() + "");
        tvDisCause.setText(pointInfo.getDis_cause() == null ? "" : pointInfo.getDis_cause() + "");
        tvImperilFamilies.setText(pointInfo.getImperil_families() + "");
        tvImperilMan.setText(pointInfo.getImperil_man() + "");
        tvMainObject.setText(pointInfo.getMain_object() == null ? "" : pointInfo.getMain_object() + "");
        tvAreaId.setText(pointInfo.getArea_id() + "");

        AlertDialog dialog1 = new AlertDialog.Builder(MainActivity.this)
                .setView(view)
                .show();
    }

    private void setOkhttpDetails(String id) {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.disaster_details))
                .addParams("id", id + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<DisasterDetailInfo>() {
                        }.getType();
                        disasterDetailInfo = gson.fromJson(response, type);
                        showDialogDetails(disasterDetailInfo);
                        WaitingDialog.closeDialog(waitingDialog);
                    }
                });
    }


    /**
     * 灾害点详细信息
     */
    private void showDialogDetails(final DisasterDetailInfo disasterDetailInfo) {
        DisasterDetailInfo.DisBasicInfoBean pointInfo = disasterDetailInfo.getDisBasicInfo().get(0);
        Log.d("limeng", "response:1111111111");
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_disaster_detail_info, null);
        final AlertDialog ss = new AlertDialog.Builder(MainActivity.this).setView(view).create();
        final LinearLayout llSwitchInfo = (LinearLayout) view.findViewById(R.id.ll_switch_info);
        MyRadioGroup myrg = (MyRadioGroup) view.findViewById(R.id.myrg);
        String info1;
        final String info2 = "隐患点名称：" + (pointInfo.getDis_name() == null ? "" : pointInfo.getDis_name()) + "\n"
                + "灾害点类型：" + (pointInfo.getDis_type() + "") + "\n"
                + "灾害点编号：" + (pointInfo.getDis_no() == null ? "" : pointInfo.getDis_no()) + "\n"
                + "乡镇：" + (pointInfo.getDis_location() == null ? "" : pointInfo.getDis_location()) + "\n"
                + "详细地址：" + (pointInfo.getDis_location() == null ? "" : pointInfo.getDis_location()) + "\n"
                + "主要诱因：" + (pointInfo.getDis_cause() == null ? "" : pointInfo.getDis_cause()) + "\n"
                + "受威胁人数：" + (pointInfo.getImperil_man()) + "\n"
                + "受威胁户数：" + (pointInfo.getImperil_families()) + "\n"
                + "影响对象：" + (pointInfo.getMain_object() == null ? "" : pointInfo.getMain_object()) + "\n"
                + "威胁财产(万元)：" + (pointInfo.getImperil_money() == null ? "" : pointInfo.getImperil_money()) + "\n"
                + "危害等级：" + (pointInfo.getImperil_level()) + "\n"
                + "处置意见：" + (pointInfo.getDeal_idea() == null ? "" : pointInfo.getDeal_idea()) + "\n"
                + "防治级别：" + (pointInfo.getDefense_level()) + "\n"
                + "稳定性：" + (pointInfo.getStable_level()) + "\n"
                + "坡度：" + (pointInfo.getDis_slope() == null ? "" : pointInfo.getDis_slope()) + "\n"
                + "面积(km²)：" + (pointInfo.getDis_area() == null ? "" : pointInfo.getDis_area()) + "\n"
                + "体积(m³)：" + (pointInfo.getDis_volume() == null ? "" : pointInfo.getDis_volume()) + "\n"
                + "前缘高程(m)：" + (pointInfo.getDis_before() == null ? "" : pointInfo.getDis_before()) + "\n"
                + "后缘高程(m)：" + (pointInfo.getDis_after() == null ? "" : pointInfo.getDis_after()) + "\n"
                + "经纬度：" + (pointInfo.getDis_lon() == null ? "" : pointInfo.getDis_lon()) + "," + (pointInfo.getDis_lat() == null ? "" : pointInfo.getDis_lat()) + "\n"
                + "入库时间：" + (pointInfo.getCome_time() == null ? "" : pointInfo.getCome_time());
        Log.d("limeng", "info2：" + info2);
        myrg.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
                llSwitchInfo.removeAllViews();
                switch (checkedId) {
                    case R.id.rb_detail_1_1:
                        llSwitchInfo.addView(addPersonView(disasterDetailInfo.getPersonsMessage()));
                        break;
                    case R.id.rb_detail_1_2:
                        llSwitchInfo.addView(addTextView(info2));
                        break;
                    case R.id.rb_detail_1_3:
                        llSwitchInfo.addView(addTextView("暂无图片"));
                        break;
                    case R.id.rb_detail_1_4:
                        llSwitchInfo.addView(addTextView("暂无视频"));
                        break;
                    case R.id.rb_detail_2_1:
                        llSwitchInfo.addView(addTableView1(disasterDetailInfo));
                        break;
                    case R.id.rb_detail_2_2:
                        llSwitchInfo.addView(addTableView2(disasterDetailInfo));
                        break;
                    case R.id.rb_detail_2_3:
                        llSwitchInfo.addView(addTableView3(disasterDetailInfo));
                        break;
                    case R.id.rb_detail_2_4:
                        break;
                    case R.id.rb_detail_3_1:
                        break;
                    case R.id.rb_detail_3_2:
                        break;
                    case R.id.rb_detail_3_3:
                        break;
                }
            }
        });

        ss.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                rg.check(R.id.rbtn_base_info);
            }
        });
        ss.show();

    }


    /**
     * 添加TextView
     *
     * @return
     */
    private View addPersonView(List<DisasterDetailInfo.PersonsMessageBean> personsMessageBean) {
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);       //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      LayoutInflater inflater2 = getLayoutInflater();
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.activity_recycleview, null);
        view.setLayoutParams(lp);
        RecyclerView rc = (RecyclerView) view.findViewById(R.id.rc_disaster_photo);
        //传入所有列数的最小公倍数，1和4的最小公倍数为4，即意味着每一列将被分为4格
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //设置表格，根据position计算在该position处1列占几格数据
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return 1;
            }
        });
        List<String> mList = new ArrayList<>();
        mList.add("1");
        mList.add("2");
        mList.add("3");
        rc.setLayoutManager(gridLayoutManager);
        rc.setAdapter(new RcPersonAdapter(this, mList));
        return view;
    }


    /**
     * 添加TextView
     *
     * @return
     */
    private View addTextView(String info) {
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);       //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      LayoutInflater inflater2 = getLayoutInflater();
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.activity_tv_text, null);
        view.setLayoutParams(lp);
        TextView tv = (TextView) view.findViewById(R.id.tv_switch_text);
        tv.setText(info);
        return view;
    }

    /**
     * 添加table1:防灾明白卡
     *
     * @return
     */
    private View addTableView1(DisasterDetailInfo disasterDetailInfo) {
        DisasterDetailInfo.FCardBean mFCard = disasterDetailInfo.getFCard().get(0);
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);       //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.activity_table_1, null);
        view.setLayoutParams(lp);
        ((TextView) view.findViewById(R.id.tv_tavle1_1)).setText(mFCard.getD_position() == null ? "" : mFCard.getD_position());
        ((TextView) view.findViewById(R.id.tv_tavle1_2)).setText(mFCard.getD_type() == null ? "" : mFCard.getD_type());
        ((TextView) view.findViewById(R.id.tv_tavle1_3)).setText(mFCard.getD_induce_factor() == null ? "" : mFCard.getD_induce_factor());
        ((TextView) view.findViewById(R.id.tv_tavle1_4)).setText(mFCard.getD_threat() == null ? "" : mFCard.getD_threat());
        ((TextView) view.findViewById(R.id.tv_tavle1_5)).setText(mFCard.getD_monitor_man() == null ? "" : mFCard.getD_monitor_man());
        ((TextView) view.findViewById(R.id.tv_tavle1_6)).setText(mFCard.getD_monitor_phone() == null ? "" : mFCard.getD_monitor_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_7)).setText(mFCard.getD_monitor_sign() == null ? "" : mFCard.getD_monitor_sign());
        ((TextView) view.findViewById(R.id.tv_tavle1_8)).setText(mFCard.getD_alarm_type() == null ? "" : mFCard.getD_alarm_type());
        ((TextView) view.findViewById(R.id.tv_tavle1_9)).setText(mFCard.getD_monitor_judge() == null ? "" : mFCard.getD_monitor_judge());
        ((TextView) view.findViewById(R.id.tv_tavle1_10)).setText(mFCard.getD_e_place() == null ? "" : mFCard.getD_e_place());
        ((TextView) view.findViewById(R.id.tv_tavle1_11)).setText(mFCard.getD_e_signal() == null ? "" : mFCard.getD_e_signal());
        ((TextView) view.findViewById(R.id.tv_tavle1_12)).setText(mFCard.getD_e_line() == null ? "" : mFCard.getD_e_line());
        ((TextView) view.findViewById(R.id.tv_tavle1_13)).setText(mFCard.getD_exclude_man() == null ? "" : mFCard.getD_exclude_man());
        ((TextView) view.findViewById(R.id.tv_tavle1_14)).setText(mFCard.getD_exclude_phone() == null ? "" : mFCard.getD_exclude_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_15)).setText(mFCard.getD_security_man() == null ? "" : mFCard.getD_security_man());
        ((TextView) view.findViewById(R.id.tv_tavle1_16)).setText(mFCard.getD_security_phone() == null ? "" : mFCard.getD_security_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_17)).setText(mFCard.getD_doc_man() == null ? "" : mFCard.getD_doc_man());
        ((TextView) view.findViewById(R.id.tv_tavle1_18)).setText(mFCard.getD_doc_phone() == null ? "" : mFCard.getD_doc_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_19)).setText(mFCard.getD_grant_unit() == null ? "" : mFCard.getD_grant_unit());
        ((TextView) view.findViewById(R.id.tv_tavle1_20)).setText(mFCard.getD_hold_unit() == null ? "" : mFCard.getD_hold_unit());
        ((TextView) view.findViewById(R.id.tv_tavle1_21)).setText(mFCard.getD_grant_phone() == null ? "" : mFCard.getD_grant_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_22)).setText(mFCard.getD_hold_phone() == null ? "" : mFCard.getD_hold_phone());
        ((TextView) view.findViewById(R.id.tv_tavle1_23)).setText(mFCard.getD_grant_date() == null ? "" : mFCard.getD_grant_date());
        ((TextView) view.findViewById(R.id.tv_tavle1_24)).setText(mFCard.getD_hold_date() == null ? "" : mFCard.getD_hold_date());


        return view;
    }

    /**
     * 添加table2:
     *
     * @return
     */
    private View addTableView2(DisasterDetailInfo disasterDetailInfo) {
        DisasterDetailInfo.HedgeCardBean mHedgeCard = disasterDetailInfo.getHedgeCard().get(0);
        List<DisasterDetailInfo.Family0Bean> family0Beans = disasterDetailInfo.getFamily0();
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);       //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.activity_table_2, null);
        view.setLayoutParams(lp);
        ((TextView) view.findViewById(R.id.tv_table2_1)).setText(mHedgeCard.getH_family_name() == null ? "" : mHedgeCard.getH_family_name());
        ((TextView) view.findViewById(R.id.tv_table2_2)).setText(mHedgeCard.getH_family_num() == null ? "" : mHedgeCard.getH_family_num());
        ((TextView) view.findViewById(R.id.tv_table2_3)).setText(mHedgeCard.getH_house_type() == null ? "" : mHedgeCard.getH_house_type());
        ((TextView) view.findViewById(R.id.tv_table2_4)).setText(mHedgeCard.getH_address() == null ? "" : mHedgeCard.getH_address());
        for (int i = 0; i < family0Beans.size(); i++) {
            switch (i) {
                case 0:
                    ((TextView) view.findViewById(R.id.tv_table2_5)).setText(family0Beans.get(i).getM_name_one() == null ? "" : family0Beans.get(i).getM_name_one());
                    ((TextView) view.findViewById(R.id.tv_table2_6)).setText(family0Beans.get(i).getM_sex_one() == null ? "" : family0Beans.get(i).getM_sex_one());
                    ((TextView) view.findViewById(R.id.tv_table2_7)).setText(family0Beans.get(i).getM_age_one() == null ? "" : family0Beans.get(i).getM_age_one());
                    break;
                case 1:
                    ((TextView) view.findViewById(R.id.tv_table2_8)).setText(family0Beans.get(i).getM_name_one() == null ? "" : family0Beans.get(i).getM_name_one());
                    ((TextView) view.findViewById(R.id.tv_table2_9)).setText(family0Beans.get(i).getM_sex_one() == null ? "" : family0Beans.get(i).getM_sex_one());
                    ((TextView) view.findViewById(R.id.tv_table2_10)).setText(family0Beans.get(i).getM_age_one() == null ? "" : family0Beans.get(i).getM_age_one());
                    break;
                case 2:
                    ((TextView) view.findViewById(R.id.tv_table2_11)).setText(family0Beans.get(i).getM_name_one() == null ? "" : family0Beans.get(i).getM_name_one());
                    ((TextView) view.findViewById(R.id.tv_table2_12)).setText(family0Beans.get(i).getM_sex_one() == null ? "" : family0Beans.get(i).getM_sex_one());
                    ((TextView) view.findViewById(R.id.tv_table2_13)).setText(family0Beans.get(i).getM_age_one() == null ? "" : family0Beans.get(i).getM_age_one());
                    break;
                case 3:
                    ((TextView) view.findViewById(R.id.tv_table2_14)).setText(family0Beans.get(i).getM_name_one() == null ? "" : family0Beans.get(i).getM_name_one());
                    ((TextView) view.findViewById(R.id.tv_table2_15)).setText(family0Beans.get(i).getM_sex_one() == null ? "" : family0Beans.get(i).getM_sex_one());
                    ((TextView) view.findViewById(R.id.tv_table2_16)).setText(family0Beans.get(i).getM_age_one() == null ? "" : family0Beans.get(i).getM_age_one());
                    break;
            }
        }
        ((TextView) view.findViewById(R.id.tv_table2_17)).setText(mHedgeCard.getH_dis_type() == null ? "" : mHedgeCard.getH_dis_type());
        ((TextView) view.findViewById(R.id.tv_table2_18)).setText(mHedgeCard.getH_dis_scale() == null ? "" : mHedgeCard.getH_dis_scale());
        ((TextView) view.findViewById(R.id.tv_table2_19)).setText(mHedgeCard.getH_dis_relationship() == null ? "" : mHedgeCard.getH_dis_relationship());
        ((TextView) view.findViewById(R.id.tv_table2_20)).setText(mHedgeCard.getH_dis_factor() == null ? "" : mHedgeCard.getH_dis_factor());
        ((TextView) view.findViewById(R.id.tv_table2_21)).setText(mHedgeCard.getH_dis_matters() == null ? "" : mHedgeCard.getH_dis_matters());
        ((TextView) view.findViewById(R.id.tv_table2_22)).setText(mHedgeCard.getH_pre_man() == null ? "" : mHedgeCard.getH_pre_man());
        ((TextView) view.findViewById(R.id.tv_table2_23)).setText(mHedgeCard.getH_pre_phone() == null ? "" : mHedgeCard.getH_pre_phone());
        ((TextView) view.findViewById(R.id.tv_table2_24)).setText(mHedgeCard.getH_pre_signal() == null ? "" : mHedgeCard.getH_pre_signal());
        ((TextView) view.findViewById(R.id.tv_table2_25)).setText(mHedgeCard.getH_signal_man() == null ? "" : mHedgeCard.getH_signal_man());
        ((TextView) view.findViewById(R.id.tv_table2_26)).setText(mHedgeCard.getH_signal_phone() == null ? "" : mHedgeCard.getH_signal_phone());
        ((TextView) view.findViewById(R.id.tv_table2_27)).setText(mHedgeCard.getH_eva_line() == null ? "" : mHedgeCard.getH_eva_line());
        ((TextView) view.findViewById(R.id.tv_table2_28)).setText(mHedgeCard.getH_eva_placement() == null ? "" : mHedgeCard.getH_eva_placement());
        ((TextView) view.findViewById(R.id.tv_table2_29)).setText(mHedgeCard.getH_placement_man() == null ? "" : mHedgeCard.getH_placement_man());
        ((TextView) view.findViewById(R.id.tv_table2_30)).setText(mHedgeCard.getH_placement_phone() == null ? "" : mHedgeCard.getH_placement_phone());
        ((TextView) view.findViewById(R.id.tv_table2_31)).setText(mHedgeCard.getH_ambulance_unit() == null ? "" : mHedgeCard.getH_ambulance_unit());
        ((TextView) view.findViewById(R.id.tv_table2_32)).setText(mHedgeCard.getH_ambulance_man() == null ? "" : mHedgeCard.getH_ambulance_man());
        ((TextView) view.findViewById(R.id.tv_table2_33)).setText(mHedgeCard.getH_ambulance_phone() == null ? "" : mHedgeCard.getH_ambulance_phone());
        ((TextView) view.findViewById(R.id.tv_table2_34)).setText(mHedgeCard.getH_grant_unit() == null ? "" : mHedgeCard.getH_grant_unit());
        ((TextView) view.findViewById(R.id.tv_table2_35)).setText(mHedgeCard.getH_holder() == null ? "" : mHedgeCard.getH_holder());
        ((TextView) view.findViewById(R.id.tv_table2_36)).setText(mHedgeCard.getH_grant_phone() == null ? "" : mHedgeCard.getH_grant_phone());
        ((TextView) view.findViewById(R.id.tv_table2_37)).setText(mHedgeCard.getH_holder_phone() == null ? "" : mHedgeCard.getH_holder_phone());
        ((TextView) view.findViewById(R.id.tv_table2_38)).setText(mHedgeCard.getH_grant_date() == null ? "" : mHedgeCard.getH_grant_date());
        ((TextView) view.findViewById(R.id.tv_table2_39)).setText(mHedgeCard.getH_holder_date() == null ? "" : mHedgeCard.getH_holder_date());

        return view;
    }


    /**
     * 添加table2:
     *
     * @return
     */
    private View addTableView3(DisasterDetailInfo disasterDetailInfo) {
        DisasterDetailInfo.HedgeCardBean mHedgeCard = disasterDetailInfo.getHedgeCard().get(0);
        // TODO 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);       //LayoutInflater inflater1=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      LayoutInflater inflater2 = getLayoutInflater();
        LayoutInflater inflater3 = LayoutInflater.from(this);
        View view = inflater3.inflate(R.layout.activity_table_3, null);
        view.setLayoutParams(lp);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_area_back:
                setAreaBack();
                break;
            case R.id.iv_data_back:
                setDataBack();
                break;
            case R.id.ll_rainfall:
                llMoreStateBefore = llMoreState;
                llMoreState = 1;
                setRainfallMore();
                setDisasterLegend(R.layout.activity_rainfall_legend, 1);
                if (llMoreStateBefore != 1) {
                    //waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
                    layers.clear();
                    elevationSources.clear();
                    graphicsOverlays.clear();
                    layers.add(vectorLayer);
                    Camera camera = new Camera(28.769167, 106.910399, 50000.0, 0, 20, 0.0);
                    sceneView.setViewpointCamera(camera);
                }
                break;
            case R.id.ll_dangerpoint:
                llMoreStateBefore = llMoreState;
                llMoreState = 2;
                setRainfallMore();
                setDisasterLegend(R.layout.activity_disaster_legend, 2);
                if (llMoreStateBefore != 2) {
                    // waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
                    layers.clear();
                    elevationSources.clear();
                    graphicsOverlays.clear();
                    initData();
                    layers.add(lowImageLayer);
                    layers.add(highImageLayer);
                    elevationSources.add(elevationSource);
                    Camera camera = new Camera(28.769167, 106.910399, 50000.0, 0, 20, 0.0);
                    sceneView.setViewpointCamera(camera);
                }
                break;
            case R.id.ll_staff:
                llMoreStateBefore = llMoreState;
                llMoreState = 3;
                setRainfallMore();
                if (view != null) {
                    rlMain.removeView(view);
                }
                if (llMoreStateBefore != 3) {
                    layers.clear();
                    elevationSources.clear();
                    graphicsOverlays.clear();
                    initPersonData();
                    layers.add(dianziLayer);
                    Camera camera = new Camera(28.769167, 106.910399, 50000.0, 0, 20, 0.0);
                    sceneView.setViewpointCamera(camera);
                }
                break;
            case R.id.ll_equipment:
                llMoreStateBefore = llMoreState;
                llMoreState = 4;
                setRainfallMore();
                if (view != null) {
                    rlMain.removeView(view);
                }
                addEquipment();
                if (llMoreStateBefore != 4) {
                    layers.clear();
                    elevationSources.clear();
                    graphicsOverlays.clear();
                    layers.add(lowImageLayer);
                    layers.add(highImageLayer);
                    elevationSources.add(elevationSource);
                    Camera camera = new Camera(28.769167, 106.910399, 50000.0, 0, 20, 0.0);
                    sceneView.setViewpointCamera(camera);
                }
                break;
            case R.id.ll_qxsy:
                llMoreStateBefore = llMoreState;
                llMoreState = 5;
                setRainfallMore();
                if (view != null) {
                    rlMain.removeView(view);
                }
                break;

        }
    }

    private void initPersonData() {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.get_person_location))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();
                        WaitingDialog.closeDialog(waitingDialog);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject oj = object.getJSONObject("data");
                            JSONArray array = oj.getJSONArray("群测群防人");
                            for (int i = 0; i < array.length() - 1; i++) {
                                JSONObject o = array.getJSONObject(i);
                                PersonLocation personLocation = new PersonLocation();
                                personLocation.setId(o.getInt("id"));
                                personLocation.setLat(o.getString("dis_lat"));
                                personLocation.setLon(o.getString("dis_lon"));
                                qcPersons.add(personLocation);
                            }
                            setPersonGraphic();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setPersonGraphic() {
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.person);
        final PictureMarkerSymbol symbol = new PictureMarkerSymbol(drawable);
        symbol.setWidth(30);
        symbol.setHeight(30);
        symbol.setOffsetY(11);
        symbol.loadAsync();
        symbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                for (PersonLocation disasterPoint : qcPersons) {
                    Point point = new Point(Double.valueOf(disasterPoint.getLon()), Double.valueOf(disasterPoint.getLat()), SpatialReferences.getWgs84());
                    Graphic graphic = new Graphic(point, symbol);
                    graphic.setZIndex(disasterPoint.getId());
                    qcGraphics.add(graphic);
                }

            }

        });
    }

    private void setDisasterLegend(@LayoutRes int resource, int type) {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.rightMargin = 10;
        lp.bottomMargin = 30;
        if (view == null) {
            view = inflater.inflate(resource, null);
            view.setLayoutParams(lp);
            rlMain.addView(view);
        } else {
            rlMain.removeView(view);
            view = inflater.inflate(resource, null);
            view.setLayoutParams(lp);
            rlMain.addView(view);

        }
        if (type == 2) {
            LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.ll_legend_1);
            LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.ll_legend_2);
            LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.ll_legend_3);
            LinearLayout linearLayout4 = (LinearLayout) view.findViewById(R.id.ll_legend_4);
            LinearLayout linearLayout5 = (LinearLayout) view.findViewById(R.id.ll_legend_5);
            LinearLayout linearLayout6 = (LinearLayout) view.findViewById(R.id.ll_legend_6);
            LinearLayout linearLayout7 = (LinearLayout) view.findViewById(R.id.ll_legend_7);
            linearLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allHuaPOGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notHuaPOGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conHuaPOGraphics);
                    }
                }
            });
            linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allNiSHILiuGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notNiSHILiuGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conNiSHILiuGraphics);
                    }
                }
            });
            linearLayout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allWeiYanGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notWeiYanGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conWeiYanGraphics);
                    }
                }
            });
            linearLayout4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allXiePoGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notXiePoGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conXiePoGraphics);
                    }
                }
            });
            linearLayout5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allTanTaGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notTanTaGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conTaAnGraphics);
                    }
                }
            });
            linearLayout6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allLieFengGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notLieFengGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conLieFengGraphics);
                    }
                }
            });
            linearLayout7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rbDisasterPoint.isChecked()) {
                        updateGraphic(allTaAnGraphics);
                    } else if (rbHandledPoint.isChecked()) {
                        updateGraphic(notTaAnGraphics);
                    } else if (rbCanceledPoint.isChecked()) {
                        updateGraphic(conTaAnGraphics);
                    }
                }
            });

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.rb_ssyl:
                Log.d(TAG, "实时雨量打开了:" + b);
                if (b) {
                    layers.add(ssYLLayer);
                } else {
                    layers.remove(ssYLLayer);
                }
                break;
            case R.id.rb_yldzx:
                Log.d(TAG, "雨量等值线打开了:" + b);
                if (b) {
                    layers.add(dengZXLayer);
                } else {
                    layers.remove(dengZXLayer);
                }
                break;
            case R.id.rb_disaster_point:
                Log.d(TAG, "所有的灾害点集合大小：" + allGraphics.size() + "\n"
                        + "已消耗灾害点集合大小：" + consumedGraphics.size() + "\n"
                        + "未消耗灾害点集合大小：" + notConsumeGraphics.size());
                if (b) {
                    updateGraphic(allGraphics);
                }
                break;
            case R.id.rb_canceled_point://已消耗
                if (b) {
                    updateGraphic(consumedGraphics);
                }
                break;
            case R.id.rb_handled_point:
                if (b) {
                    updateGraphic(notConsumeGraphics);
                }
                break;
            case R.id.rb_qcqf_person:
                if (b) {
                    updatePersonGraphic(qcGraphics);
                }
                break;
            case R.id.rb_zs_person:
                if (b) {
                    updateLocalGraphic(zsGraphics);
                }
                break;
            case R.id.rb_pq_person:
                if (b) {
                    updateLocalGraphic(pqGraphics);
                }
                break;
            case R.id.rb_dhz_person:
                if (b) {
                    updateLocalGraphic(dhzGraphics);
                }
                break;
            case R.id.rb_equipment_jiance:
                if (b) {
                    updateEquipmentGraphic(jianceGraphics);
                }
                break;
            case R.id.rb_jinqiao:
                if (b) {
                    ElevationSource elevationSource = new ArcGISTiledElevationSource(getResources().getString(R.string.jinqiao_elevation_url));
                    layers.add(jinQiaoLayer);
                    elevationSources.add(elevationSource);
                    Camera camera = new Camera(29.07337764118905, 106.8774290607224, 2000, 0, 0, 0.0);
                    sceneView.setViewpointCamera(camera);
                } else {
                    layers.remove(jinQiaoLayer);
                    elevationSources.remove(elevationSource);
                }
                break;
            case R.id.rb_shilin:
                if (b) {
                    ElevationSource elevationSource = new ArcGISTiledElevationSource(getResources().getString(R.string.shilin_elevation_url));
                    layers.add(shiLinLayer);
                    elevationSources.add(elevationSource);
                    Camera camera = new Camera(28.87312428984992, 106.91015726332898, 2000, 0, 0, 0.0);
                    sceneView.setViewpointCamera(camera);
                } else {
                    layers.remove(shiLinLayer);
                    elevationSources.remove(elevationSource);
                }
                break;
        }
    }



    private void addEquipment() {
        BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.equipment);
        final PictureMarkerSymbol symbol = new PictureMarkerSymbol(drawable);
        symbol.setWidth(50);
        symbol.setHeight(50);
        symbol.setOffsetY(11);
        symbol.loadAsync();
        symbol.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                Point point = new Point(106.91015726332898, 28.87312428984992, SpatialReferences.getWgs84());
                Graphic graphic = new Graphic(point, symbol);
                graphic.setZIndex(100000);
                jianceGraphics.add(graphic);
            }

        });
    }

    private void updateLocalGraphic(List<Graphic> g) {
        localGraphics.clear();
        localGraphics.addAll(g);
        graphicsOverlays.clear();
        graphicsOverlays.add(localGraphicsOverlay);
    }

    private void updatePersonGraphic(List<Graphic> q) {
        personGraphics.clear();
        personGraphics.addAll(q);
        graphicsOverlays.clear();
        graphicsOverlays.add(personGraphicsOverlay);
    }
    private void updateEquipmentGraphic(List<Graphic> graphics) {
        equipmentGraphics.clear();
        equipmentGraphics.addAll(graphics);
        graphicsOverlays.clear();
        graphicsOverlays.add(equipmentGraphicOverlay);
    }
    /**
     * 更新地图上的图标
     *
     * @param g 图标集合
     */
    private void updateGraphic(List<Graphic> g) {
        graphics.clear();
        graphics.addAll(g);
        graphicsOverlays.clear();
        graphicsOverlays.add(graphicsOverlay);
    }

    private void initData() {
        waitingDialog = WaitingDialog.createLoadingDialog(this, "正在请求中...");
        OkHttpUtils.get().url(getResources().getString(R.string.get_disaster_point))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getApplicationContext(), "网络连接失败！", Toast.LENGTH_SHORT).show();
                        WaitingDialog.closeDialog(waitingDialog);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        WaitingDialog.closeDialog(waitingDialog);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<DisasterPoint>>() {
                        }.getType();
                        disasterPoints = gson.fromJson(response, type);
                        Log.d("WSD", "集合大小：" + disasterPoints.size() + "\n数据:" + disasterPoints);
                        setOverlay();
                    }
                });
    }

    private void setOverlay() {
        BitmapDrawable huapo = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_1);
        BitmapDrawable nishiliu = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_2);
        BitmapDrawable weiyan = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_3);
        BitmapDrawable xiepo = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_4);
        BitmapDrawable tanta = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_5);
        BitmapDrawable liefeng = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_6);
        BitmapDrawable taan = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.point_7);
        List<BitmapDrawable> drawables = new ArrayList<>();
        drawables.add(huapo);
        drawables.add(nishiliu);
        drawables.add(weiyan);
        drawables.add(xiepo);
        drawables.add(tanta);
        drawables.add(liefeng);
        drawables.add(taan);
        for (int i = 1; i <= 7; i++) {
            final PictureMarkerSymbol pinStarBlueSymbol = new PictureMarkerSymbol(drawables.get(i - 1));
            //Optionally set the size, if not set the image will be auto sized based on its size in pixels,
            //its appearance would then differ across devices with different resolutions.
            pinStarBlueSymbol.setHeight(40);
            pinStarBlueSymbol.setWidth(40);
            //Optionally set the offset, to align the base of the symbol aligns with the point geometry
            pinStarBlueSymbol.setOffsetY(
                    11); //The image used for the symbol has a transparent buffer around it, so the offset is not simply height/2
            pinStarBlueSymbol.loadAsync();
            //[DocRef: END]
            final int finalI = i;
            pinStarBlueSymbol.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    for (DisasterPoint disasterPoint : disasterPoints) {
                        if (disasterPoint.getDis_type() == finalI) {
                            Point point = new Point(Double.valueOf(disasterPoint.getDis_lon()), Double.valueOf(disasterPoint.getDis_lat()), SpatialReferences.getWgs84());
                            Graphic graphic = new Graphic(point, pinStarBlueSymbol);
                            graphic.setZIndex(disasterPoint.getId());
                            allGraphics.add(graphic);
                            switch (finalI) {
                                case 1:
                                    allHuaPOGraphics.add(graphic);
                                    break;
                                case 2:
                                    allNiSHILiuGraphics.add(graphic);
                                    break;
                                case 3:
                                    allWeiYanGraphics.add(graphic);
                                    break;
                                case 4:
                                    allXiePoGraphics.add(graphic);
                                    break;
                                case 5:
                                    allTanTaGraphics.add(graphic);
                                    break;
                                case 6:
                                    allLieFengGraphics.add(graphic);
                                    break;
                                case 7:
                                    allTaAnGraphics.add(graphic);
                                    break;
                            }
                            if (disasterPoint.getDis_state() == 1) {
                                notConsumeGraphics.add(graphic);
                                switch (finalI) {
                                    case 1:
                                        notHuaPOGraphics.add(graphic);
                                        break;
                                    case 2:
                                        notNiSHILiuGraphics.add(graphic);
                                        break;
                                    case 3:
                                        notWeiYanGraphics.add(graphic);
                                        break;
                                    case 4:
                                        notXiePoGraphics.add(graphic);
                                        break;
                                    case 5:
                                        notTanTaGraphics.add(graphic);
                                        break;
                                    case 6:
                                        notLieFengGraphics.add(graphic);
                                        break;
                                    case 7:
                                        notTaAnGraphics.add(graphic);
                                        break;
                                }
                            } else {
                                consumedGraphics.add(graphic);
                                switch (finalI) {
                                    case 1:
                                        conHuaPOGraphics.add(graphic);
                                        break;
                                    case 2:
                                        conNiSHILiuGraphics.add(graphic);
                                        break;
                                    case 3:
                                        conWeiYanGraphics.add(graphic);
                                        break;
                                    case 4:
                                        conXiePoGraphics.add(graphic);
                                        break;
                                    case 5:
                                        conTanTaGraphics.add(graphic);
                                        break;
                                    case 6:
                                        conLieFengGraphics.add(graphic);
                                        break;
                                    case 7:
                                        conTaAnGraphics.add(graphic);
                                        break;
                                }
                            }
                        }
                    }
                }
            });
        }

    }

    private void setRainfallMore() {
        switch (llMoreState) {
            case 1:
                rgRainfall.setVisibility(View.VISIBLE);
                rgDangerpoint.setVisibility(View.GONE);
                rgStaff.setVisibility(View.GONE);
                rgEquipment.setVisibility(View.GONE);
                rgQxsy.setVisibility(View.GONE);
                break;
            case 2:
                rgRainfall.setVisibility(View.GONE);
                rgDangerpoint.setVisibility(View.VISIBLE);
                rgStaff.setVisibility(View.GONE);
                rgEquipment.setVisibility(View.GONE);
                rgQxsy.setVisibility(View.GONE);
                break;
            case 3:
                rgRainfall.setVisibility(View.GONE);
                rgDangerpoint.setVisibility(View.GONE);
                rgStaff.setVisibility(View.VISIBLE);
                rgEquipment.setVisibility(View.GONE);
                rgQxsy.setVisibility(View.GONE);
                break;
            case 4:
                rgRainfall.setVisibility(View.GONE);
                rgDangerpoint.setVisibility(View.GONE);
                rgStaff.setVisibility(View.GONE);
                rgEquipment.setVisibility(View.VISIBLE);
                rgQxsy.setVisibility(View.GONE);
                break;
            case 5:
                rgRainfall.setVisibility(View.GONE);
                rgDangerpoint.setVisibility(View.GONE);
                rgStaff.setVisibility(View.GONE);
                rgEquipment.setVisibility(View.GONE);
                rgQxsy.setVisibility(View.VISIBLE);
                break;

        }


        if (llMoreState != llMoreStateBefore) {
            rgRainfall.clearCheck();
            rgDangerpoint.clearCheck();
            rgStaff.clearCheck();
            rgEquipment.clearCheck();
            rgQxsy.clearCheck();
            Log.d("limeng", "llMoreState:" + llMoreState + "\n" + "llMoreStateBefore:" + llMoreStateBefore);
            ObjectAnimator animator3 = null;
            ObjectAnimator animator4 = null;
            switch (llMoreState) {
                case 1:
                    animator3 = ObjectAnimator.ofFloat(ivRainfallMore, "rotation", 0, 90);
                    break;
                case 2:
                    animator3 = ObjectAnimator.ofFloat(ivDangerpointMore, "rotation", 0, 90);
                    break;
                case 3:
                    animator3 = ObjectAnimator.ofFloat(ivStaffMore, "rotation", 0, 90);
                    break;
                case 4:
                    animator3 = ObjectAnimator.ofFloat(ivEquipmentMore, "rotation", 0, 90);
                    break;
                case 5:
                    animator3 = ObjectAnimator.ofFloat(ivQxsy, "rotation", 0, 90);
                    break;
            }
            if (animator3 != null) {
                animator3.setDuration(100);
                animator3.start();
            }
            switch (llMoreStateBefore) {
                case 1:
                    animator4 = ObjectAnimator.ofFloat(ivRainfallMore, "rotation", 90, 0);
                    break;
                case 2:
                    animator4 = ObjectAnimator.ofFloat(ivDangerpointMore, "rotation", 90, 0);
                    break;
                case 3:
                    animator4 = ObjectAnimator.ofFloat(ivStaffMore, "rotation", 90, 0);
                    break;
                case 4:
                    animator4 = ObjectAnimator.ofFloat(ivEquipmentMore, "rotation", 90, 0);
                    break;
                case 5:
                    animator4 = ObjectAnimator.ofFloat(ivQxsy, "rotation", 90, 0);
            }
            if (animator4 != null) {
                animator4.setDuration(100);
                animator4.start();
            }
        }

    }

    private void setMoreState() {
        if (llAreaState == false) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivAreaBack, "rotation", 0, 180);
            animator1.setDuration(100);
            animator1.start();
            llAreaState = true;
        } else {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivAreaBack, "rotation", 180, 0);
            animator1.setDuration(100);
            animator1.start();
            llAreaState = false;
        }
    }

    private void setAreaBack() {
        if (llAreaState == false) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(llArea, "x", 0, -(llArea.getWidth() - 70));
            animator.setDuration(1000);
            animator.start();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivAreaBack, "rotation", 0, 180);
            animator1.setDuration(100);
            animator1.start();
            llAreaState = true;
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(llArea, "x", -(llArea.getWidth() - 70), 0);
            animator.setDuration(1000);
            animator.start();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivAreaBack, "rotation", 180, 0);
            animator1.setDuration(100);
            animator1.start();
            llAreaState = false;
        }
    }

    private void setDataBack() {
        if (llDataState == false) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(llData, "x", 0, -(llData.getWidth() - 70));
            animator.setDuration(1000);
            animator.start();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivDataBack, "rotation", 0, 180);
            animator1.setDuration(100);
            animator1.start();
            llDataState = true;
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(llData, "x", -(llData.getWidth() - 70), 0);
            animator.setDuration(1000);
            animator.start();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(ivDataBack, "rotation", 180, 0);
            animator1.setDuration(100);
            animator1.start();
            llDataState = false;
        }
    }


}
