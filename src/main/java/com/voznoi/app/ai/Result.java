package com.voznoi.app.ai;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import weka.core.Instance;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Result {
    double distance;
    int[] row;
    Instance instance;
    int desiredCell;
    public int getResult(){
        return row[row.length-1];
    }
}
