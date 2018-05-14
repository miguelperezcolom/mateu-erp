package io.mateu.common.consolidador;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.hotel.GetAvailableHotelsRS;

@Getter@Setter
public class Consolidador {

    private ModeloConsolidador modelo;

    public Consolidador(ModeloConsolidador modelo) {
        this.modelo = modelo;
    }

    public void procesar(DispoRQ rq, Consumer<GetAvailableHotelsRS> callback) {

        //Flowable.just(rq).subscribe(System.out::println);

        Flowable<GetAvailableHotelsRS> source = Flowable.fromCallable(() -> {
            return new Logica().procesar(getModelo(), rq);
        });

        Flowable<GetAvailableHotelsRS> runBackground = source.subscribeOn(Schedulers.io());

        Flowable<GetAvailableHotelsRS> showForeground = runBackground.observeOn(Schedulers.single());

        showForeground.subscribe(callback, Throwable::printStackTrace);

        //Flowable.just("Hello portfolio").subscribe(System.out::println);

    }

}
