package com.example.burge.homeworkrxj.Controllers;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class RxViewEvents {

    public static Observable<String> getTextChangeObservable(TextView editText) {

        final Subject<String> subject = BehaviorSubject.create();

        subject.onNext(editText.getText().toString());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                subject.onNext(s.toString());
            }
        });

        return subject.publish().autoConnect().doOnSubscribe(disposable -> subject.onNext(editText.getText().toString()));

    }

}
