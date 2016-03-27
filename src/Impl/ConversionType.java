package Impl;

/**
 * @author Okeke Paul
 * Created by paulex on 27/03/16.
 */
public enum ConversionType {
    DEFAULT(0), SINGLE(1), LIST(2);
    int value;
    ConversionType(int value){
        this.value = value;
    }
}
