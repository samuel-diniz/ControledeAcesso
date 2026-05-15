package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.dto.CheckInRequest;
import br.edu.unicid.controledeacesso.dto.CheckInResponse;
import br.edu.unicid.controledeacesso.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public CheckInResponse validar(@RequestBody CheckInRequest request) {
        return checkInService.validar(request.getToken(), request.getDispositivo());
    }
}
