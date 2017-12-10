package com.example.john.baidumap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.john.baidumap.overlayutil.BikingRouteOverlay;
import com.example.john.baidumap.overlayutil.DrivingRouteOverlay;
import com.example.john.baidumap.overlayutil.TransitRouteOverlay;
import com.example.john.baidumap.overlayutil.WalkingRouteOverlay;
import static android.R.attr.paddingBottom;
import static android.R.attr.paddingLeft;
import static android.R.attr.paddingRight;
import static android.R.attr.paddingTop;

//参考自http://www.jianshu.com/p/7cf34278d279
public class MainActivity extends AppCompatActivity {
    MapView mMapView = null;
    public LocationClient mLocationClient = null;
    public MyLocationListener mLocationListener ;
    public static final String TAG = "location";
    private BaiduMap mBaidumap;
    //显示定位点
    private BitmapDescriptor mMarker;
    private TextView textView = null;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;
    private BitmapDescriptor mIconLocation;
    private MyLocationConfiguration.LocationMode mLocationMode;
    private UiSettings mUiSettings;
    private Button button1,button2,button3,button4;
    private RoutePlanSearch mSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();
        mMarker = BitmapDescriptorFactory.fromResource(R.mipmap.arrow);

        mSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                // 获取步行线路规划结果  
                if(walkingRouteResult == null||walkingRouteResult.error!=SearchResult.ERRORNO.NO_ERROR){
                    Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
                }
                if(walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息  
                    walkingRouteResult.getSuggestAddrInfo();
                    Log.d("baiduMap", "起终点或途经点地址有岐义");
                    return;
                }
                if(walkingRouteResult.error==SearchResult.ERRORNO.NO_ERROR){
                    WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(walkingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }
            @Override
            public void onGetTransitRouteResult(TransitRouteResult result) {
                // 获取公交换乘路径规划结果
                if(result == null||result.error!=SearchResult.ERRORNO.NO_ERROR){
                    Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
                }
                if(result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息  
                    result.getSuggestAddrInfo();
                    return;
                }
                if(result.error == SearchResult.ERRORNO.NO_ERROR){
                    TransitRouteOverlay overlay = new TransitRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                mSearch.destroy();
            }
            public void onGetDrivingRouteResult(DrivingRouteResult result) {
                if(result == null||result.error!=SearchResult.ERRORNO.NO_ERROR){
                    Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
                }
                if(result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    result.getSuggestAddrInfo();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR){
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
                mSearch.destroy();
            }
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }
            @Override
            public void onGetBikingRouteResult(BikingRouteResult result) {
                if(result == null||result.error!=SearchResult.ERRORNO.NO_ERROR){
                    Toast.makeText(MainActivity.this,"抱歉，未找到结果",Toast.LENGTH_SHORT).show();
                }
                if(result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR){
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    result.getSuggestAddrInfo();
                    return;
                }
                if(result.error == SearchResult.ERRORNO.NO_ERROR){
                    BikingRouteOverlay overlay = new BikingRouteOverlay(mBaidumap);
                    mBaidumap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
                mSearch.destroy();
            }
        };
        mSearch.setOnGetRoutePlanResultListener(listener);

        //开启步行规划
        button1 = (Button)findViewById(R.id.walking);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
                PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "百度科技园");
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
            }
        });

        //开启驾车规划
        button2 = (Button)findViewById(R.id.driving);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
                PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "百度科技园");
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
            }
        });

        //骑行规划
        button3 = (Button)findViewById(R.id.bus);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
                PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");
                mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
            }
        });

        //骑行规划
        button4 = (Button)findViewById(R.id.riding);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");
                PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "西单");
                mSearch.bikingSearch((new BikingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
            }
        });
        //初始化定位信息
        initLocation();
        initView();
    }

    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            mLongitude=113.5549;
            mLatitude=22.23;
            MyLocationData data = new MyLocationData.Builder()//
                    .direction(100)//
                    .accuracy(bdLocation.getRadius())//
                    .latitude(mLatitude)//
                    .longitude(mLongitude)//
                    .build();
            mBaidumap.setMyLocationData(data);

            MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, false, mMarker, 0xAAFFFF88, 0xAAFFFF88);
            mBaidumap.setMyLocationConfiguration(config);


            if(isFirstIn){
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaidumap.animateMapStatus(msu);
                isFirstIn = false;

                Toast.makeText(MainActivity.this, bdLocation.getAddrStr().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initLocation() {
        Log.d(TAG,"开始初始化");
        //开启定位图层
        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaidumap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        //地图Logo
        mMapView.setLogoPosition(LogoPosition.logoPostionleftBottom);
        //地图Logo不允许遮挡
        mBaidumap.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        //指南针
        mUiSettings = mBaidumap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        //比例尺
        mMapView. showScaleControl(true);
        mMapView.getMapLevel();
        //缩放按钮
        mMapView. showZoomControls(true);
        //地图缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        //地图平移
        mUiSettings. setScrollGesturesEnabled(true);
        //地图俯视
        mUiSettings. setOverlookingGesturesEnabled(true);
        //地图旋转
        mUiSettings .setRotateGesturesEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);

        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.arrow);
    }

    private void initView() {
        //显示的比例
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(18f);
        mBaidumap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaidumap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }

        //myOrientationListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaidumap.setMyLocationEnabled(false);
        mLocationClient.stop();

        //myOrientationListener.stop();
    }
}
