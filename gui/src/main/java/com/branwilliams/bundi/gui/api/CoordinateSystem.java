package com.branwilliams.bundi.gui.api;

public interface CoordinateSystem <X, Y> {

    int toPixelsX(X x);

    int toPixelsY(Y y);

}
