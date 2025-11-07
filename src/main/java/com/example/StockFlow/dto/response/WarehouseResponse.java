package com.example.StockFlow.dto.response;

import lombok.Data;

@Data
public class WarehouseResponse {

        private Long id;
        private String name;
        private String location;
        private boolean active;

        // Infos du manager (pas la liste des entrepôts)
        private String managerName;
        private String managerEmail;

}
