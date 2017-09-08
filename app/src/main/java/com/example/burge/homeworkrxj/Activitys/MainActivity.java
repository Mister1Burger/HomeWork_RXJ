package com.example.burge.homeworkrxj.Activitys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.burge.homeworkrxj.Controllers.ListOfCars;
import com.example.burge.homeworkrxj.Controllers.RxViewEvents;
import com.example.burge.homeworkrxj.Activitys.R;
import com.example.burge.homeworkrxj.Activitys.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    ListOfCars listOfCars = new ListOfCars();


    @BindView(R2.id.tv_1)
    EditText tv1;
    @BindView(R2.id.tv_2)
    EditText tv2;
    @BindView(R2.id.tv_3)
    EditText tv3;

    @BindView(R2.id.fab)
    FloatingActionButton fab;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actOfDialog();


    }

    @Override
    protected void onResume() {
        super.onResume();
        disposables.add(Observable.combineLatest(
                RxViewEvents
                        .getTextChangeObservable(tv1)
                        .map(value -> !TextUtils.isEmpty(value))

                ,
                RxViewEvents
                        .getTextChangeObservable(tv2)
                        .map(value -> !TextUtils.isEmpty(value))

                ,
                RxViewEvents
                        .getTextChangeObservable(tv3)
                        .map(value -> value.matches("d{4}"))

                ,
                (f1, f2, f3) -> f1 && f2 && f3)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setSaveButtonVisible)
        );
    }

    private void setSaveButtonVisible(boolean flag) {
        fab.setEnabled(flag);
    }


    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void actWithCars() {

        disposables.add(listOfCars.isFlag()
                .doOnNext(aBoolean -> Log.d("TAG", String.valueOf(aBoolean)))
                .flatMap(aBoolean -> listOfCars.getCars(aBoolean))
                .doOnNext(cars -> Log.d("TAG", String.valueOf(cars.size())))
//                .subscribeOn(Schedulers.io())
//                .map(this::sortConversation)
                .flatMap(Observable::fromIterable)
                .filter(car -> car.getSpeed() > 150)
                .toList()
                .toObservable()
                .doOnNext(cars -> Log.d("TAG", String.valueOf(cars.size())))
                //               .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cars -> {
                }, throwable -> {
                }));


    }

    public void actOfDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Hello");
        builder.setMessage("Message");

        builder.setCancelable(true);

        builder.setNegativeButton("False", (dialogInterface, i) -> {
            listOfCars.setFlag(false);
            actWithCars();
        });
        builder.setPositiveButton("True", (dialogInterface, i) -> {
            listOfCars.setFlag(true);
            actWithCars();
        });
        builder.show();
    }
}
