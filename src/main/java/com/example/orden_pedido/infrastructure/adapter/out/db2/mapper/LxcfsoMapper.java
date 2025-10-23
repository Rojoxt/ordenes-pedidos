package com.example.orden_pedido.infrastructure.adapter.out.db2.mapper;

import com.example.orden_pedido.domain.aggregate.OrderDetail;
import com.example.orden_pedido.infrastructure.adapter.out.db2.dto.LxcfsoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ObjectFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LxcfsoMapper {

    @Mapping(target = "fsogui", source = "gui")
    @Mapping(target = "fsolin", source = "orderDetail.lineNumber")
    @Mapping(target = "fsoslo", constant = "1")
    @Mapping(target = "fsofac", constant = "MX")
    @Mapping(target = "fsopro", source = "orderDetail.material")
    @Mapping(target = "fsoalm", source = "warehouse")
    @Mapping(target = "fsoreq", source = "orderDetail.requestedQuantity", qualifiedByName = "bigDecimalToInt")
    @Mapping(target = "fsofdu", source = "orderDetail.expirationDate")
    @Mapping(target = "fsofre", source = "orderDetail.releaseDate")     // Fecha_lanzamiento -> fsofre (Conversión de Integer a String para mantener formato AAAAMMDD)
    @Mapping(target = "fsolot", source = "orderDetail.batchNumber")
    @Mapping(target = "fsocom", source = "orderDetail.factoryOrder")
    @Mapping(target = "fsoco2", source = "orderDetail.orderClass")
    LxcfsoDTO toDto(OrderDetail orderDetail, String gui, String warehouse);


    List<LxcfsoDTO> toDtoList(List<OrderDetail> orderDetails, String gui, String warehouse);

    @ObjectFactory
    default LxcfsoDTO createLxcfsoDTO(
            String fsogui, int fsolin, int fsoslo, String fsofac, String fsopro,
            String fsoalm, int fsoreq, String fsofdu, String fsofre, String fsolot,
            String fsocom, String fsoco2)
    {
        // Se invoca el constructor canónico del Record
        return new LxcfsoDTO(
                fsogui, fsolin, fsoslo, fsofac, fsopro, fsoalm, fsoreq,
                fsofdu, fsofre, fsolot, fsocom, fsoco2);
    }
    @ObjectFactory
    default <T> List<T> createList() {
        return new ArrayList<>();
    }

    // --- Métodos de Ayuda (QualifiedByName) ---

    @Named("bigDecimalToInt")
    default int bigDecimalToInt(BigDecimal value) {
        if (value == null) {
            return 0; // O lanzar una excepción, según la regla de negocio
        }
        return value.intValue();
    }
}
