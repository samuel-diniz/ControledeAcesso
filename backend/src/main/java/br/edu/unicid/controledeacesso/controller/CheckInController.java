package br.edu.unicid.controledeacesso.controller;

import br.edu.unicid.controledeacesso.dto.CheckInRequest;
import br.edu.unicid.controledeacesso.dto.CheckInResponse;
import br.edu.unicid.controledeacesso.service.CheckInService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "*")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public CheckInResponse validar(@RequestBody CheckInRequest request) {
        return checkInService.validar(request.getToken(), request.getDispositivo(), request.getTipo());
    }
}
