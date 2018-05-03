package co.appguardian.peerfy.services.util;

import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static final Long SEGUNDOS_POR_DIA = 86400l;

    public static boolean isToday(Long lastTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastTime);

        Long days = calculateDaysBetween(Calendar.getInstance().getTime(), calendar.getTime());

        if (days == 0) {
            return true;
        } else {
            return false;
        }
    }
    public static Long calculateDaysBetween(Date fechaInicial, Date fechaFinal) {

        long segundos;

        fechaInicial = deleteTimeStamp(fechaInicial);
        fechaFinal = deleteTimeStamp(fechaFinal);

        segundos = TimeUnit.SECONDS.convert(fechaFinal.getTime(), TimeUnit.MILLISECONDS)
                - TimeUnit.SECONDS.convert(fechaInicial.getTime(), TimeUnit.MILLISECONDS);

        return segundos / SEGUNDOS_POR_DIA;
    }
    public static Date deleteTimeStamp(Date fecha) {

        Calendar cl = Calendar.getInstance();
        cl.setTime(fecha);

        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);

        return cl.getTime();
    }
}
