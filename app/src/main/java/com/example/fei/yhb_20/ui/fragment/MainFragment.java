package com.example.fei.yhb_20.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.ThumbnailListener;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.ui.DeatilActivity;
import com.example.fei.yhb_20.ui.GalleryUrlActivity;
import com.example.fei.yhb_20.ui.PersonalActivity;
import com.example.fei.yhb_20.utils.ACache;
import com.example.fei.yhb_20.utils.ExpressionUtil;
import com.example.fei.yhb_20.utils.MyUtils;
import com.marshalchen.common.ui.recyclerviewitemanimator.SlideInOutBottomItemAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private static RecyclerView recyclerView;
    private static Picasso picasso;
    private static ACache aCache;
    LinearLayoutManager layoutManager;
    private SharedPreferences sharedPreferences;
    private static LinearLayout llFoot;
    private static Button send;
    private static EditText comment;
    private static Post currentPost;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 2;
    int firstVisibleItem;
    int visibleItemCount;
    int totalItemCount;
    private DrawerLayout ll_container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int curPage = 0;
    private static final int limit = 3;
    private MyAdapter myAdapter;
    private List<Post> datas;
    private static ArrayList<String> everAccessPaths;
    public static ArrayList<Boolean> isCompleteds;
    private static ArrayList<String> arrayList;
    private static String[] paths;

    public MainFragment() {
    }

    ;
    private BaseUser baseUser;

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //比较Activity根布局与当前布局的大小
            int heightDiff = ll_container.getRootView().getHeight() - ll_container.getHeight();
            if (heightDiff > 200) {
                //大小超过100时，一般为显示虚拟键盘事件
                getActivity().findViewById(R.id.footer).setVisibility(View.GONE);
                llFoot.setVisibility(View.VISIBLE);
                Log.e(TAG, "显示");
            } else {
                Log.e(TAG, "隐藏");
                getActivity().findViewById(R.id.footer).setVisibility(View.VISIBLE);
                llFoot.setVisibility(View.GONE);
//                LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main,null).findViewById(R.id.team_singlechat_id_foot).setVisibility(View.GONE);
                //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(TAG, "onCreateView is invoked");

        everAccessPaths = new ArrayList<>();
        isCompleteds = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!swipeRefreshLayout.isRefreshing()) {
                    Log.d(TAG, "ignore manually update!");
                } else {
//                    loadPage();成功得到数据之后就停止刷新
                    refreshView();
                }
            }
        });


        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView mrecyclerView, int dx, int dy) {
                super.onScrolled(mrecyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();        //
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.d(TAG, String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));

                Log.d(TAG, "visibleItemCount:" + visibleItemCount + "totalItemCount:" + totalItemCount + "firstVisibleItem:" + firstVisibleItem + "previousTotal:" + previousTotal);

                if ((totalItemCount - 1) == layoutManager.findLastCompletelyVisibleItemPosition()) {
                    if (mrecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Log.d(TAG, "dragging");
                    } else if (mrecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                        Log.d(TAG, "idle");
                    } else if (mrecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        Log.d(TAG, "settling");
                    }

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            //每一次都更新previousTotal，记录总共有多少条记录已经绑定到adapter上去了
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        // 在这里添加加载更多的代码
                        Log.d(TAG, "need to load more");
                        query();
                        loading = true;
                    }
                }


            }
        });
        ll_container = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        send = (Button) view.findViewById(R.id.team_singlechat_id_send);
        comment = (EditText) view.findViewById(R.id.team_singlechat_id_edit);
        send.setEnabled(false);
        ImageView face = (ImageView) view.findViewById(R.id.team_singlechat_id_expression);
        llFoot = (LinearLayout) view.findViewById(R.id.team_singlechat_id_foot);

        ll_container.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);


        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(TAG, "before" + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged" + s);
                if (TextUtils.isEmpty(s)) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "after");
            }
        });

        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.showFaceDialog(getActivity(), comment);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                llFoot.setVisibility(View.INVISIBLE);
                MyUtils.commentSend(currentPost, comment, getActivity());
                //强制隐藏
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                comment.setText(null);
            }
        });


        picasso = Picasso.with(getActivity());
        //TODO 不要在这里联网查询
//        recyclerView.setAdapter(new MyAdapter(data));
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO 在这里联网
        baseUser = BmobUser.getCurrentUser(getActivity(), BaseUser.class);
        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
        refreshView();
    }


    /**
     * 联网进行刷新并写入磁盘
     */
    public void refreshView() {
        Log.d(TAG, "mainfragment");
        curPage = 0;
        everAccessPaths.clear();
        swipeRefreshLayout.setRefreshing(true);

        BmobQuery<Post> query = new BmobQuery<>();
        if (baseUser.getMyInfo() != null) {
            if (baseUser.getMyInfo().getBlockers() != null) {
                query.addWhereNotContainedIn("ownerId", baseUser.getMyInfo().getBlockers());
            }
        }
        query.include("user");
            query.setLimit(limit);
            query.order("-createdAt");
            query.findObjects(getActivity(), new FindListener<Post>() {
                @Override
                public void onSuccess(final List<Post> posts) {
                    //在这里写入缓存
                    datas = posts;
                    for (int i = 0; i < posts.size(); i++) {
                        aCache.put(String.valueOf(i), posts.get(i));
                        if (aCache.getAsBinary(posts.get(i).getObjectId() + "footerBoolean") == null) {
                            byte[] footerBoolean = {1, 1, 1, 1};
                            aCache.put(posts.get(i).getObjectId() + "footerBoolean", footerBoolean);
                        }
                        //根据ObjectId来
                        Log.e(TAG, "write success " + i);
                    }
                    aCache.put("cacheSize", String.valueOf(posts.size()));

                    //就是把这个去掉就行
                    myAdapter = new MyAdapter(datas, getActivity());
                    recyclerView.setAdapter(myAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(int err, String s) {
                    Log.i(TAG, s + err);
                    Log.e(TAG, s);
                    if (sharedPreferences.getBoolean("ever", false)) {
                        List<Post> objects = new ArrayList<Post>();

                        int size = Integer.parseInt(aCache.getAsString("cacheSize"));
                        Post post;
                        for (int i = 0; i < size; i++) {
                            post = (Post) aCache.getAsObject(String.valueOf(i));
                            if (post == null) {
                                android.util.Log.e(TAG, "post is null");
                            }
                            objects.add(post);
                        }

                        datas = objects;
                        myAdapter = new MyAdapter(datas, getActivity());
                        recyclerView.setAdapter(myAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(getActivity(), "您没有登录过，没有缓存文件！", Toast.LENGTH_LONG).show();
                    }
                }
            });


    }

    /**
     * 现在先不用管上载加载更多的功能实现，以后再说吧，应该不难;能加载一次，加载一次后就不能成功了，是curpage的问题
     */
    public void query() {
        swipeRefreshLayout.setRefreshing(true);
            BmobQuery<Post> query = new BmobQuery<>();
        query.include("user");
        if (baseUser.getMyInfo() != null) {
            if (baseUser.getMyInfo().getBlockers() != null) {
                query.addWhereNotContainedIn("ownerId", baseUser.getMyInfo().getBlockers());
            }
            }
            query.setLimit(limit);
            query.order("-createdAt");
            Log.d(TAG + "curPage", String.valueOf(curPage));
            query.setSkip((curPage + 1) * limit);
            query.findObjects(getActivity(), new FindListener<Post>() {
                @Override
                public void onSuccess(final List<Post> posts) {
                    if (posts != null) {
                        //在这里写入缓存
                        datas = posts;
                        curPage++;
                        //这个是写入磁盘，不用管理了，已经完善
                        for (int i = 0; i < posts.size(); i++) {
                            Log.d(TAG, posts.get(i).toString());
                            aCache.put(String.valueOf(i), posts.get(i));
                            if (aCache.getAsBinary(posts.get(i).getObjectId() + "footerBoolean") == null) {
                                byte[] footerBoolean = {1, 1, 1, 1};
                                aCache.put(posts.get(i).getObjectId() + "footerBoolean", footerBoolean);
                            }
                            Log.e(TAG, "write success " + i);
                        }
                        aCache.put("cacheSize", String.valueOf(posts.size()));
                        Log.d(TAG + "liang", String.valueOf(posts.size()));

                        myAdapter.addQuote(posts);
                        swipeRefreshLayout.setRefreshing(false);
                        //只要能执行到这里来就能刷新
                        loading = false;
                    }

                }

                @Override
                public void onError(int err, String s) {
                    Log.i(TAG, s + err);
                    Log.e(TAG, s);
                    if (sharedPreferences.getBoolean("ever", false)) {
                        List<Post> objects = new ArrayList<Post>();

                        int size = Integer.parseInt(aCache.getAsString("cacheSize"));
                        Post post;
                        for (int i = 0; i < size; i++) {
                            post = (Post) aCache.getAsObject(String.valueOf(i));
                            if (post == null) {
                                android.util.Log.e(TAG, "post is null");
                            }
                            objects.add(post);
                        }
                        datas = objects;
                        myAdapter.addQuote(datas);
                        swipeRefreshLayout.setRefreshing(false);
                        loading = false;
                    } else {
                        Toast.makeText(getActivity(), "您没有登录过，没有缓存文件！", Toast.LENGTH_LONG).show();
                    }
                }
            });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aCache = ACache.get(getActivity());
    }

    /**
     * 这个adapter太过于庞大了，以后应该尽量与视图分开写，而且adapter中也要实现更多的功能
     */
    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private static final int LIKE = 0;
        private static final int DISLIKE = 1;
        private static final int COMMENT = 2;
        private int lastPosition = -1;
        private List<Post> data;
        private Context context;

        public MyAdapter(List<Post> data, Context context) {
            this.data = data;
            this.context = context;
        }

        /**
         * 在下面append 之前的post
         *
         * @param q
         */
        private void addQuote(List<Post> q) {
            for (Post post : q) {
                data.add(post);
            }
            notifyItemRangeInserted(data.size() - 1, q.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item, viewGroup, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            //从data中获取数据，填充入视图中,处理获取的数据
            final Post post = data.get(i);
            if (post != null) {
                final String objectId = post.getObjectId();
                final String userId = post.getUser().getObjectId();
                final String cacheBooleanKey = objectId + "footerBoolean";
                final ArrayList<Integer> numberFooter = post.getNumberFooter();
                final byte[] footerBoolean = aCache.getAsBinary(cacheBooleanKey);
                //要显示表情
                String zhengze = "f0[0-9]{2}|f10[0-7]";
                SpannableString spannableString = ExpressionUtil.getExpressionString(context, post.getContent(), zhengze);
                viewHolder.content.setText(spannableString);

                viewHolder.userName.setText(post.getUser().getUsername());// 级联查询查找username
                Log.d(TAG, post.getUser().getUsername());

                String merchantName = "";
                if (post.getMerchantName().equals("") || post.getMerchantName() == null) {
                    //do nothing
                    Log.d(TAG, "merchantName is null");
                } else {
                    merchantName = "  " + post.getMerchantName() + "  ";
                }
                viewHolder.merchantName.setText(merchantName);

                viewHolder.tvLike.setText(String.valueOf(numberFooter.get(LIKE)));
                viewHolder.tvDislike.setText(String.valueOf(numberFooter.get(DISLIKE)));
                viewHolder.tvConment.setText(String.valueOf(post.getCommentItems().size()));

                BmobQuery<Merchant> query = new BmobQuery<Merchant>();
                query.getObject(context, post.getUser().getObjectId(), new GetListener<Merchant>() {
                    @Override
                    public void onSuccess(Merchant merchant) {
                        if (merchant.getAvatarPaht() != null) {
                            Picasso.with(context).load(merchant.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(45, 45).into(viewHolder.avata);
                        } else {
                            Toast.makeText(context, "获取头像失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e(TAG, s + i);
                    }
                });

                viewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DeatilActivity.class);
                        intent.putExtra("post", post);
                        intent.putExtra("sourceActivity", "MainFragment");
                        context.startActivity(intent);

                    }
                });

                /**
                 *定义菜单事件
                 */
                viewHolder.list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyUtils.showPopupMenu(context, post.getObjectId(), userId);
                    }
                });

                /**
                 * 喜欢
                 */
                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyUtils.footerCommand(footerBoolean, LIKE, viewHolder.ivLike, viewHolder.tvLike, numberFooter, aCache, post, context, objectId);
                    }
                });

                /**
                 * 没有帮助
                 */
                viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyUtils.footerCommand(footerBoolean, DISLIKE, viewHolder.ivDislike, viewHolder.tvDislike, numberFooter, aCache, post, context, objectId);
                    }
                });

                /**
                 * 评论
                 */
                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //得到当前的post
                        currentPost = post;
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        llFoot.setVisibility(View.VISIBLE);
                        comment.requestFocus();
                        //强制显示键盘
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

                /**
                 * 分享事件
                 */
                viewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "我有好东西与您分享" + post.getContent());
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                });

                //点击头像进入个人主页
                viewHolder.avata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("post", post);
                        intent.putExtra("user", post.getUser());
                        context.startActivity(intent);
                    }
                });
                //点击姓名进入个人主页
                viewHolder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("user", post.getUser());
                        context.startActivity(intent);
                    }
                });

                /**
                 * 设置展示的时候的图标颜色值，要正确的显示
                 */
                Log.e(TAG, Arrays.toString(footerBoolean));
                if (footerBoolean != null) {
                    if (footerBoolean[DISLIKE] == 0) {
                        viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike_pressed);
                    }
                    if (footerBoolean[LIKE] == 0) {
                        viewHolder.ivLike.setImageResource(R.drawable.icon_heart_pressed);
                    }
                }


                //格式化时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(post.getCreatedAt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewHolder.time.setText(MyUtils.timeLogic(date, context));

                //获取图片，使用Picasso可以缓存
                paths = post.getPaths().split("\\|");
                int t = paths.length;
                Log.e(TAG, String.valueOf(t));


                arrayList = post.getThumnailsName();
                Log.e(TAG, arrayList.toString());

                /**
                 * 想获取缩略图，但是没有成功
                 * 问题解决了，是因为没有进行sign认证，所以没有有400错误
                 * 循环的添加总是有问题
                 * 各种方法都没有修复图片的重复的问题
                 */
//                getPic(0,viewHolder.gallery);
                for (int i1 = 0; i1 < arrayList.size(); i1++) {
                    final int finalI = i1;
                    BmobProFile.getInstance(context).submitThumnailTask(arrayList.get(i1), 1, new ThumbnailListener() {
                        @Override
                        public void onSuccess(final String thumbnailName, String thumbnailUrl) {
                            /**
                             * 解决了图片的重复加载的问题,没有完全解决，还可以优化,还是有问题
                             * !everAccessPaths.contains(thumbnailName) ||
                             */
                            if (!everAccessPaths.contains(thumbnailName) || viewHolder.gallery.getChildCount() != arrayList.size()) {
                                Log.d(TAG + "fei", i + ":" + viewHolder.gallery.getChildCount() + ":" + arrayList.size());
                                ImageView imageView = new ImageView(context);
                                picasso.load(BmobProFile.getInstance(context).signURL(thumbnailName, thumbnailUrl, "54f197dc6dce11fc7c078c07420a080e", 0, null)).placeholder(R.drawable.ic_launcher).resize(200, 200).into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        everAccessPaths.add(thumbnailName);
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(context, "网络连接缓慢", Toast.LENGTH_LONG).show();
                                        //do nothing
                                    }
                                });
                                imageView.setPadding(3, 3, 3, 3);
                                viewHolder.gallery.addView(imageView);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, GalleryUrlActivity.class);
                                        intent.putExtra("photoUrls", paths);
                                        intent.putExtra("currentItem", finalI);
                                        context.startActivity(intent);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            Log.e(TAG, errormsg);
                        }
                    });

                }
            }
            setAnimation(viewHolder.container, i);

        }

        private void getPic(final int position, final LinearLayout gallery) {
            BmobProFile.getInstance(context).submitThumnailTask(arrayList.get(position), 1, new ThumbnailListener() {
                @Override
                public void onSuccess(final String thumbnailName, String thumbnailUrl) {
                    /**
                     * 解决了图片的重复加载的问题,没有完全解决，还可以优化,还是有问题
                     * !everAccessPaths.contains(thumbnailName) ||
                     */
                    if (isCompleteds.isEmpty() || !isCompleteds.get(position)) {
                        Log.d(TAG + "fei", position + ":" + gallery.getChildCount() + ":" + arrayList.size());
                        ImageView imageView = new ImageView(context);
                        picasso.load(BmobProFile.getInstance(context).signURL(thumbnailName, thumbnailUrl, "54f197dc6dce11fc7c078c07420a080e", 0, null)).placeholder(R.drawable.ic_launcher).resize(200, 200).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                everAccessPaths.add(thumbnailName);
                                int pos = position + 1;
                                if ((position + 1) < arrayList.size()) {
                                    getPic(pos, gallery);
                                } else {
                                    isCompleteds.add(true);
                                }
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(context, "网络连接缓慢", Toast.LENGTH_LONG).show();
                                //do nothing
                            }
                        });
                        imageView.setPadding(3, 3, 3, 3);
                        gallery.addView(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, GalleryUrlActivity.class);
                                intent.putExtra("photoUrls", paths);
//                                intent.putExtra("currentItem", finalI);
                                context.startActivity(intent);
                            }
                        });

                    }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    Log.e(TAG, errormsg);
                }
            });
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView merchantName, userName, content, time, tvLike, tvDislike, tvConment;
            ImageView avata, share, list, ivLike, ivDislike, ivComment;
            LinearLayout like, dislike, comment, gallery;
            CardView container;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                merchantName = (TextView) itemView.findViewById(R.id.tv_main_merchantName);
                userName = (TextView) itemView.findViewById(R.id.tv_main_postName);
                content = (TextView) itemView.findViewById(R.id.tv_main_content);
                time = (TextView) itemView.findViewById(R.id.tv_main_time);
                avata = (ImageView) itemView.findViewById(R.id.iv_main_logo);
                like = (LinearLayout) itemView.findViewById(R.id.ll_main_like);
                dislike = (LinearLayout) itemView.findViewById(R.id.ll_main_dislike);
                comment = (LinearLayout) itemView.findViewById(R.id.ll_main_conment);
                tvLike = (TextView) itemView.findViewById(R.id.tv_main_like);
                tvDislike = (TextView) itemView.findViewById(R.id.tv_main_dislike);
                tvConment = (TextView) itemView.findViewById(R.id.tv_main_comment);
                share = (ImageView) itemView.findViewById(R.id.iv_main_share);
                list = (ImageView) itemView.findViewById(R.id.iv_main_list);
                gallery = (LinearLayout) itemView.findViewById(R.id.ll_gallery);
                ivComment = (ImageView) itemView.findViewById(R.id.iv_main_comment);
                ivDislike = (ImageView) itemView.findViewById(R.id.iv_main_dislike);
                ivLike = (ImageView) itemView.findViewById(R.id.iv_main_like);

                container = (CardView) itemView.findViewById(R.id.container);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        everAccessPaths.clear();
    }
}
