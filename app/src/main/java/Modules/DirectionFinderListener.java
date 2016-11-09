package Modules;

import java.util.List;

/**
 * Created by Nguyen Tan Luan on 10/21/2016.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
