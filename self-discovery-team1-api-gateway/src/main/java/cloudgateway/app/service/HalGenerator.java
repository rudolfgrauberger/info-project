package cloudgateway.app.service;

import java.util.List;

public interface HalGenerator {

   String getHalString(List<String> json);
}
