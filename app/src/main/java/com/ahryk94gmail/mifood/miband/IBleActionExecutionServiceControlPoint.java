package com.ahryk94gmail.mifood.miband;

import java.util.List;

public interface IBleActionExecutionServiceControlPoint {

    boolean addToQueue(IBleAction bleAction);

    boolean addToQueue(List<IBleAction> bleActions);
}
