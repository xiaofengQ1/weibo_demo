package com.example.weibo_zhuyufeng.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weibo_zhuyufeng.Adapter.CustomOkHttpClient;
import com.example.weibo_zhuyufeng.Adapter.HomeAdapter;
import com.example.weibo_zhuyufeng.Model.GetResponseBody;
import com.example.weibo_zhuyufeng.Model.LogMessage;
import com.example.weibo_zhuyufeng.Model.LogoutMessage;
import com.example.weibo_zhuyufeng.Model.WeiboBody;
import com.example.weibo_zhuyufeng.Model.WeiboInfo;
import com.example.weibo_zhuyufeng.Model.WeiboPage;
import com.example.weibo_zhuyufeng.R;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendFragment extends Fragment {
    private static final String BASE_URL = "https://hotfix-service-prod.g.mi.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private List<WeiboInfo> weiboInfoList = new ArrayList<>();
    private List<WeiboInfo> newWeiboInfoList = new ArrayList<>();
    private static String LOGIN_STATUS = "login_status";
    private OkHttpClient client = new OkHttpClient();
    private View view;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private TextView meiwang;
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String PREF_WEIBO_LIST = "weibo_list";
    private int current = 1;
    TextView failText;

    private ConnectivityManager.NetworkCallback networkCallback;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterNetworkCallback();
    }
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }
    private void unregisterNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 创建 NetworkCallback 实例
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                // 网络连接可用时的处理逻辑
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                });
            }
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meiwang.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        // 注册 NetworkCallback
        registerNetworkCallback();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        meiwang = view.findViewById(R.id.meiyouwang_text);
        linearLayout1 = view.findViewById(R.id.recommend_linear1);
        linearLayout2 = view.findViewById(R.id.recommend_linear2);
        linearLayout3 = view.findViewById(R.id.recommend_linear3);
        failText = view.findViewById(R.id.fail_text2);
        recyclerView = view.findViewById(R.id.recycler_view_container);
        weiboInfoList.clear();
        sharedPreferences = requireActivity().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        current = 1;
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                if (msg.what == 1) {
                    homeAdapter = new HomeAdapter(weiboInfoList, sharedPreferences);
                    recyclerView.setAdapter(homeAdapter);
                    return true;
                }
                else if (msg.what == 2) {
                    if (newWeiboInfoList.isEmpty()) {
                        Toast.makeText(getContext(), "无更多内容", Toast.LENGTH_SHORT).show();
                    }else {
                        weiboInfoList.addAll(newWeiboInfoList);
                        homeAdapter.notifyDataSetChanged();
                    }
                    return true;
                }else if (msg.what == 3) {
                    List<WeiboInfo> list = loadWeiboList(getContext());
                    weiboInfoList.clear();
                    weiboInfoList.addAll(list);
                    homeAdapter = new HomeAdapter(weiboInfoList, sharedPreferences);
                    recyclerView.setAdapter(homeAdapter);
                }else if (msg.what == 4) {
                    meiwang.setVisibility(View.GONE);
                    swipeRefreshLayout.setEnabled(true);
                }else if (msg.what == 5) {
                    swipeRefreshLayout.setEnabled(false);
                }else if (msg.what == 6) {
                    List<WeiboInfo> list = loadWeiboList(getContext());
                    weiboInfoList.clear();
                    weiboInfoList.addAll(list);
                    homeAdapter = new HomeAdapter(weiboInfoList, sharedPreferences);
                    recyclerView.setAdapter(homeAdapter);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                    meiwang.setVisibility(View.VISIBLE);
                }else if (msg.what == 7) {
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    meiwang.setVisibility(View.VISIBLE);
                }else if (msg.what == 8) {
                    Collections.shuffle(weiboInfoList);
                    homeAdapter.notifyDataSetChanged();
                    saveWeiboList(weiboInfoList);
                    swipeRefreshLayout.setRefreshing(false);
                }
                return true;
            }
        });
        failText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("yyy", "onClick: ");
                if (checkNetworkStatus(getContext())) {
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.VISIBLE);
                }
            }
        });
        load();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGetData(1, 10);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == weiboInfoList.size() - 1) {
                    loadGetData(current+1, 10);
                    current++;
                }
            }
        });
        editor.putBoolean("isFirstRun", false);
        editor.apply();
        return view;
    }
    public void saveWeiboList(List<WeiboInfo> weiboList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(weiboList);
        editor.putString(PREF_WEIBO_LIST, json);
        editor.apply();
    }
    public List<WeiboInfo> loadWeiboList(Context context) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PREF_WEIBO_LIST, null);
        Type type = new TypeToken<List<WeiboInfo>>() {}.getType();
        List<WeiboInfo> weiboList = gson.fromJson(json, type);
        if (weiboList == null) {
            Log.d("PreferenceHelper", "No saved Weibo list found.");
        } else {
            Log.d("PreferenceHelper", "Weibo list loaded from SharedPreferences.");
        }
        return weiboList;
    }
    public void load() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLogin",false);
        long expirationTime = sharedPreferences.getLong("expirationTime", 0);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayout1.setVisibility(View.GONE);
                    if (checkNetworkStatus(getContext())) {
                        linearLayout3.setVisibility(View.VISIBLE);
                    }else {
                        linearLayout2.setVisibility(View.VISIBLE);
                    }
                }
            }, 2000);
        }else {
            if (checkNetworkStatus(getContext())) {
                handler.sendEmptyMessage(4);
                if (!isLoggedIn || System.currentTimeMillis() > expirationTime) {
                    beforeGetData(1, 10);
                } else {
                    String token = sharedPreferences.getString("token", null);
                    List<WeiboInfo> list = loadWeiboList(getContext());
                    if (token == null) {
                        if (list != null && !list.isEmpty()) {
                            handler.sendEmptyMessage(3);
                        }
                        else beforeGetData(1, 10);
                    } else {
                        afterGetData(token, 1, 10);
                    }
                }
            }
            else {
                handler.sendEmptyMessage(5);
                List<WeiboInfo> list = loadWeiboList(getContext());
                if (list != null && !list.isEmpty()) {
                    handler.sendEmptyMessage(6);
                } else {
                    handler.sendEmptyMessage(7);
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LogMessage event) {
        String token = sharedPreferences.getString("token", null);
        afterGetData(token, 1, 10);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutMessage event) {
        refreshGetData(1, 10);
    }

    public void beforeGetData(int currentPage, int pageSize) {
        String endpoint = BASE_URL + "/weibo/homePage";
        String url = endpoint + "?current=" + currentPage + "&size=" + pageSize;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    WeiboBody weiboBody = gson.fromJson(responseData, WeiboBody.class);
                    newWeiboInfoList.clear();
                    newWeiboInfoList = weiboBody.getData().getRecords();
                    weiboInfoList.clear();
                    weiboInfoList.addAll(newWeiboInfoList);
                    Log.d("yyy", weiboInfoList.get(1).getUsername());
                    handler.sendEmptyMessage(1);
                } else {
                    Log.e("getData", "Error: " + response.code() + " " + response.message());
                }
            }
        });
    }
    public void afterGetData(String token, int currentPage, int pageSize) {
        String endpoint = BASE_URL + "/weibo/homePage";
        String url = endpoint + "?current=" + currentPage + "&size=" + pageSize;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + token)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        WeiboBody weiboBody = gson.fromJson(responseData, WeiboBody.class);
                        WeiboPage weiboPage = weiboBody.getData();
                        newWeiboInfoList.clear();
                        newWeiboInfoList = weiboPage.getRecords(); // 使用临时变量保存数据
                        weiboInfoList.clear();
                        weiboInfoList.addAll(newWeiboInfoList);
                        Log.d("yyy", weiboInfoList.get(1).getUsername());
                        handler.sendEmptyMessage(1);
                    } else {
                        Log.e("getData", "Error: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void refreshGetData(int currentPage, int pageSize) {
        String endpoint = BASE_URL + "/weibo/homePage";
        String url = endpoint + "?current=" + currentPage + "&size=" + pageSize;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    WeiboBody weiboBody = gson.fromJson(responseData, WeiboBody.class);
                    newWeiboInfoList.clear();
                    newWeiboInfoList = weiboBody.getData().getRecords();
                    weiboInfoList.clear();
                    weiboInfoList.addAll(newWeiboInfoList);
                    Log.d("yyy", weiboInfoList.get(1).getUsername());
                    handler.sendEmptyMessage(8);
                } else {
                    Log.e("getData", "Error: " + response.code() + " " + response.message());
                }
            }
        });
    }
    public void loadGetData(int currentPage, int pageSize) {
        String endpoint = BASE_URL + "/weibo/homePage";
        String url = endpoint + "?current=" + currentPage + "&size=" + pageSize;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    WeiboBody weiboBody = gson.fromJson(responseData, WeiboBody.class);
                    newWeiboInfoList.clear();
                    newWeiboInfoList = weiboBody.getData().getRecords();
                    Log.d("yyy", weiboInfoList.get(1).getUsername());
                    handler.sendEmptyMessage(2);
                } else {
                    Log.e("getData", "Error: " + response.code() + " " + response.message());
                }
            }
        });
    }
    //检查网络
    public static boolean checkNetworkStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
}