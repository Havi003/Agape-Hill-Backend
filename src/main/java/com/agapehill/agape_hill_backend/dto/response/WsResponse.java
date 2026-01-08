package com.agapehill.agape_hill_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WsResponse <T> {

    private WsHeader header;
    private T body ;

}
