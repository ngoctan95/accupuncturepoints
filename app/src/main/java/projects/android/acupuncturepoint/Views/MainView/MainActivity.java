package projects.android.acupuncturepoint.Views.MainView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import projects.android.acupuncturepoint.Models.AccupuncturePointData.AccupuncturePoint;
import projects.android.acupuncturepoint.Presenters.MainPresenter.MainPresenter;
import projects.android.acupuncturepoint.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IViewMain {
    private ImageView img1, img2, img3, scrollUpInfoViewBtn;
    private Toolbar toolbar;
    private FrameLayout infoView;
    private ImageButton dismissInfoViewBtn;
    private ScrollView contentAcu;
    float scalediff;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private FloatingActionButton fab;
    private int count = 1;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private MainPresenter mainPresenter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        init();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.topMargin = 50;
        img1.setLayoutParams(layoutParams);
        img2.setLayoutParams(layoutParams);
        img3.setLayoutParams(layoutParams);


        initImgs();

        mainPresenter = new MainPresenter(this, getApplicationContext());
        List<AccupuncturePoint> acupuncturePoints = mainPresenter.findLastestAcupuncturePoints(0,0);
        Log.d("=======", String.valueOf(acupuncturePoints.size()));

    }



    @SuppressLint("ClickableViewAccessibility")
    private void eventImg(ImageView imageView) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            RelativeLayout.LayoutParams parms;
            int startwidth;
            int startheight;
            float dx = 0, dy = 0, x = 0, y = 0;
            float angle = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final ImageView view = (ImageView) v;

                ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        startwidth = parms.width;
                        startheight = parms.height;
                        dx = event.getRawX() - parms.leftMargin;
                        dy = event.getRawY() - parms.topMargin;
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }
                        d = rotation(event);

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("===========",
                                String.valueOf(event.getX()) + "=====" + String.valueOf(event.getY()));
//                        showInfoView(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            x = event.getRawX();
                            y = event.getRawY();

                            parms.leftMargin = (int) (x - dx);
                            parms.topMargin = (int) (y - dy);

                            parms.rightMargin = 0;
                            parms.bottomMargin = 0;
                            parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            parms.bottomMargin = parms.topMargin + (10 * parms.height);

                            view.setLayoutParams(parms);

                        } else if (mode == ZOOM) {

                            if (event.getPointerCount() == 2) {

                                newRot = rotation(event);
                                float r = newRot - d;
                                angle = 0;

                                x = event.getRawX();
                                y = event.getRawY();

                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * view.getScaleX();
                                    Log.d("=", String.valueOf(scale));
                                    if (scale > 0.8) {
                                        scalediff = scale;
                                        view.setScaleX(scale);
                                        view.setScaleY(scale);

                                    }
                                }

                                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();

                                x = event.getRawX();
                                y = event.getRawY();

                                parms.leftMargin = (int) ((x - dx) + scalediff);
                                parms.topMargin = (int) ((y - dy) + scalediff);

                                parms.rightMargin = 0;
                                parms.bottomMargin = 0;
                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
                                parms.bottomMargin = parms.topMargin + (10 * parms.height);

                                view.setLayoutParams(parms);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void showInfoView(double x, double y) {
        hideFloatingActionButton(fab);

        TranslateAnimation animate = new TranslateAnimation(0, 0, -infoView.getHeight(), 0);
        animate.setDuration(1000);
        animate.setFillAfter(true);
        infoView.startAnimation(animate);
        infoView.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void dismissInfoView() {
        showFloatingActionButton(fab);

        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -infoView.getHeight());
        animate.setDuration(1000);
        animate.setFillAfter(true);
        infoView.startAnimation(animate);
        infoView.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initImgs() {
        eventImg(img1);
        eventImg(img2);
        eventImg(img3);

        scrollUpInfoViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoView.getVisibility() == View.VISIBLE) {
                    if (contentAcu.getVisibility() == View.VISIBLE) {
                        scrollUpInfoViewBtn.setBackground(getResources().getDrawable(R.drawable.ic_up));
                        contentAcu.setVisibility(View.GONE);
                    } else {
                        scrollUpInfoViewBtn.setBackground(getResources().getDrawable(R.drawable.ic_down));
                        TranslateAnimation animate = new TranslateAnimation(-contentAcu.getWidth(), 0, 0, 0);
                        animate.setDuration(1000);
                        animate.setFillAfter(true);
                        contentAcu.startAnimation(animate);
                        contentAcu.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void hideFloatingActionButton(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    private void showFloatingActionButton(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }

    private void init() {
        img1 = findViewById(R.id.imgView1);
        img2 = findViewById(R.id.imgView2);
        img3 = findViewById(R.id.imgView3);
        infoView = findViewById(R.id.infoView);
        dismissInfoViewBtn = findViewById(R.id.dismissInfoViewBtn);
        scrollUpInfoViewBtn = findViewById(R.id.scrollUpInfoViewBtn);
        contentAcu = findViewById(R.id.contentAcu);
        showOrDismiss();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count == 4) {
                    count = 1;
                }
                showOrDismiss();
            }
        });
        scrollUpInfoViewBtn.setBackground(getResources().getDrawable(R.drawable.ic_down));
        infoView.setVisibility(View.GONE);
        dismissInfoViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissInfoView();
            }
        });

    }

    private void showOrDismiss() {
        switch (count) {
            case 1: {
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
            }
            break;
            case 2: {
                img2.setVisibility(View.VISIBLE);
                img1.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
            }
            break;
            case 3: {
                img3.setVisibility(View.VISIBLE);
                img2.setVisibility(View.GONE);
                img1.setVisibility(View.GONE);
            }
            break;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (item.getItemId() == android.R.id.home) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.acupuncturePoint) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Tính năng này chưa được hoàn thiện. Vui lòng chờ bản release kế tiếp")
                                .setCancelable(false)
                                .setPositiveButton("Tôi đã hiểu", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                }
            });
        }


        return true;
    }
}
