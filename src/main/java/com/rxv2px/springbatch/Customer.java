package com.rxv2px.springbatch;

import java.io.Serializable;
import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {
    private int id;
    private String name;
    private Calendar birthday;
    private int transactions;

    @Override
    public String toString() {
        return String.format(
                "#%s, %s born on %3$tb %3$te, %3$tY, finished %4$s transactions",
                id,
                name,
                birthday.getTime(),
                transactions
        );
    }
}
