package fr.karspa.hiker_thinker.controller;

import fr.karspa.hiker_thinker.dtos.EquipmentDTO;
import fr.karspa.hiker_thinker.dtos.HikeEquipmentDTO;
import fr.karspa.hiker_thinker.dtos.responses.HikeResponseDTO;
import fr.karspa.hiker_thinker.model.Equipment;
import fr.karspa.hiker_thinker.model.EquipmentCategory;
import fr.karspa.hiker_thinker.model.Hike;
import fr.karspa.hiker_thinker.services.HikeService;
import fr.karspa.hiker_thinker.utils.ResponseModel;
import fr.karspa.hiker_thinker.utils.TokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hikes")
public class HikeController {

    private HikeService hikeService;
    private TokenUtils tokenUtils;

    public HikeController(HikeService hikeService, TokenUtils tokenUtils) {
        this.hikeService = hikeService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<Hike>>> getHikes() {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<List<Hike>> response = hikeService.findAll(userId, false);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{hikeId}")
    public ResponseEntity<ResponseModel<HikeResponseDTO>> getHike(@PathVariable String hikeId) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<HikeResponseDTO> response = hikeService.findByHikeId(userId, hikeId);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



    @PostMapping("")
    public ResponseEntity<ResponseModel<Hike>> createHike(@RequestBody Hike hike) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<Hike> response = hikeService.createOne(userId, hike);

        if(response.getCode().equals("201")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{hikeId}")
    public ResponseEntity<ResponseModel<Hike>> modifyHike(@PathVariable String hikeId, @RequestBody Hike hike) {
        String userId = tokenUtils.retreiveUserId();

        hike.setId(hikeId);
        ResponseModel<Hike> response = hikeService.modifyOne(userId, hike);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{hikeId}")
    public ResponseEntity<ResponseModel<Hike>> deleteHike(@PathVariable String hikeId) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<Hike> response = hikeService.deleteOne(userId, hikeId);

        if(response.getCode().equals("204")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/{hikeId}/equipments")
    public ResponseEntity<ResponseModel<Equipment>> addEquipmentToHike(@PathVariable String hikeId, @RequestBody HikeEquipmentDTO hikeEquipmentDTO) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<Equipment> response = hikeService.addEquipment(userId, hikeId, hikeEquipmentDTO);

        if(response.getCode().equals("201")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{hikeId}/equipments/{equipmentId}")
    public ResponseEntity<ResponseModel<HikeEquipmentDTO>> modifyEquipmentCategory(@PathVariable String hikeId,
                                                                                   @RequestBody HikeEquipmentDTO hikeEquipmentDTO,
                                                                                   @PathVariable String equipmentId) {
        String userId = tokenUtils.retreiveUserId();

        hikeEquipmentDTO.setSourceId(equipmentId);
        ResponseModel<HikeEquipmentDTO> response = hikeService.modifyEquipment(userId, hikeId, hikeEquipmentDTO);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{hikeId}/equipments/{equipmentId}")
    public ResponseEntity<ResponseModel<Equipment>> modifyEquipmentCategory(@PathVariable(name = "hikeId") String hikeId, @PathVariable(name = "equipmentId") String equipmentId) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<Equipment> response = hikeService.removeEquipment(userId, hikeId, equipmentId);

        if(response.getCode().equals("204")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("/{hikeId}/categories")
    public ResponseEntity<ResponseModel<List<EquipmentCategory>>> getCategories(@PathVariable String hikeId) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<List<EquipmentCategory>> response = hikeService.getCategories(userId, hikeId);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/{hikeId}/categories")
    public ResponseEntity<ResponseModel<EquipmentCategory>> addCategory(@PathVariable String hikeId, @RequestBody EquipmentCategory equipmentCategory) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<EquipmentCategory> response = hikeService.addCategory(userId, hikeId, equipmentCategory);

        if(response.getCode().equals("201")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/{hikeId}/categories/{categoryId}")
    public ResponseEntity<ResponseModel<EquipmentCategory>> modifyCategory(@PathVariable String hikeId,
                                                                           @PathVariable(name = "categoryId") String categoryId,
                                                                           @RequestBody EquipmentCategory equipmentCategory) {
        String userId = tokenUtils.retreiveUserId();

        equipmentCategory.setId(categoryId);
        ResponseModel<EquipmentCategory> response = hikeService.modifyCategory(userId, hikeId, equipmentCategory);

        if(response.getCode().equals("200")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{hikeId}/categories/{categoryId}")
    public ResponseEntity<ResponseModel<EquipmentCategory>> removeCategory(@PathVariable String hikeId, @PathVariable(name = "categoryId") String categoryId) {
        String userId = tokenUtils.retreiveUserId();

        ResponseModel<EquipmentCategory> response = hikeService.removeCategory(userId, hikeId, categoryId);

        if(response.getCode().equals("204")){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
