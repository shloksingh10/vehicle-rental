package org.example.branch;


import lombok.Builder;
import lombok.Getter;
import org.example.vehicle.Vehicle;

import java.util.List;

@Getter
@Builder
public class Branch {
    Long id;
    String name;
    List<Vehicle.Type> supportedVehicleTypes;
}
