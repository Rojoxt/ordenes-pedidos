package com.example.orden_pedido.infrastructure.adapter.out.db2.mapper;

import com.example.orden_pedido.domain.aggregate.Component;
import com.example.orden_pedido.domain.aggregate.Material;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcmbmDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class LxcmbmMapper {


    public List<LxcmbmDTO> toLxcmbmDtoList(List<Material> materials, String gui) {

        // Se inicializa el contador global por cada llamada al metodo
        // Esto asume que cada llamada es para un nuevo 'gui'
        AtomicInteger globalCounter = new AtomicInteger(1); // MBMLIN se reinicia

        return materials.stream()
                .flatMap(material -> {
                    // Resetear contadores por cada nuevo 'Material'
                    AtomicInteger parentCounter = new AtomicInteger(1); // BSEQ inicia en 1 por material
                    AtomicInteger bubbCounter = new AtomicInteger(10); // BBUBB inicia en 10 por material

                    return material.getComponents().stream()
                            .map(component -> mapComponentToDto(
                                    material,
                                    component,
                                    gui,
                                    parentCounter,
                                    bubbCounter,
                                    globalCounter));
                })
                .collect(Collectors.toList());
    }

    @Mapping(target = "mbmgui", source = "gui")
    @Mapping(target = "mbmlin", expression = "java(getGlobalCounter(globalCounter))")
    @Mapping(target = "mbmslo", constant = "1")
    @Mapping(target = "mbmeve", constant = "CREATE")
    @Mapping(target = "bprod", source = "material.parentCode")
    //@Mapping(target = "bmbomm", constant = "10"), //por ahora no se usa
    @Mapping(target = "bmwhs", constant = "MX")
    @Mapping(target = "bseq", expression = "java(getParentCounter(parentCounter))")
    @Mapping(target = "bchld", source = "component.component")
    @Mapping(target = "bqreq", source = "component.requiredQuantity")
    @Mapping(target = "bdeff", source = "component.validityStartDate")
    @Mapping(target = "bddis", source = "component.validityEndDate")
    @Mapping(target = "bmscp", source = "component.wasteFactor")
    @Mapping(target = "bbubb", expression = "java(getBubbCounter(bubbCounter))")
    @Mapping(target = "bllot", source = "component.unitOfMeasure")
    @Mapping(target = "bmddtm", source = "material.manufacturingBaseQuantity")
    @Mapping(target = "bulot", source = "material.baseQuantityUnit")
    @Mapping(target = "bcstp", source = "component.conversionFactor")
    @Mapping(target = "bopno",source = "material.orderLine")
    public abstract LxcmbmDTO mapComponentToDto(
            Material material,
            Component component,
            String gui,
            AtomicInteger parentCounter,
            AtomicInteger bubbCounter,
            AtomicInteger globalCounter);
    // Métodos para los contadores
    protected int getGlobalCounter(AtomicInteger counter) { return counter.getAndIncrement(); }
    protected int getParentCounter(AtomicInteger counter) { return counter.getAndIncrement(); }
    protected int getBubbCounter(AtomicInteger counter) { return counter.getAndAdd(10); }

}
