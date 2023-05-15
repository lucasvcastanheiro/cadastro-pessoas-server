package com.lucas.cadastropessoas.validator;

import java.util.Date;
import java.util.Calendar;

public class ValidarDataFutura {
    public static boolean dataFutura(Date date) {
        try {
            Calendar dataAtual = Calendar.getInstance();
            Calendar dataInformada = Calendar.getInstance();
            dataInformada.setTime(date);

            return dataInformada.after(dataAtual);
        } catch (Exception error) {
            return true;
        }
    }
}
