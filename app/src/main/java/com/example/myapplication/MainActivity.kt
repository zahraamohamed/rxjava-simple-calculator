package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import bsh.Interpreter
import com.example.myapplication.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    lateinit var compositeDisposable: CompositeDisposable
    /*
   *the interpreter class convert text to operation using eval function
   * and save result in equal string
   * then use get to show output ,this function must pass to it equal string
    */
   lateinit var interpreter:Interpreter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calculator()

    }

    private fun calculator() {
        compositeDisposable= CompositeDisposable()
        interpreter= Interpreter()


           Observable.create<String> { emittor ->
            binding.input.doOnTextChanged { inputText,  start, before, count ->
                binding.output.text=""
                emittor.onNext(inputText.toString())
            }

        }.debounce(1500,TimeUnit.MILLISECONDS)
         .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

        .subscribe({ t ->
        // because app  crash when edit text be empty
        // to solve this problem i make if statement
            if(t.isNotBlank()) {

                interpreter.eval("equal=$t")
                binding.output.text = interpreter.get("equal").toString().toDouble().toString()
            }
            else{
                Toast.makeText(applicationContext, "enter the equation", Toast.LENGTH_LONG).show()

            }
        },
            { e ->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
            }).add(compositeDisposable)
    }




    fun Disposable.add(compositeDisposable: CompositeDisposable) =
        compositeDisposable.add(this)


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }






















//this function write a single line code that makes an observable
// which emit a Character from A to Z with 1 second between each emit

//    fun foo(){
//        val letterObservable = Observable.just("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("")).flatMap {string-> Observable.fromIterable(string) }
//            .zipWith(Observable.interval(0, 1, TimeUnit.SECONDS), { letter: Any?, time: Long? -> letter })}


}