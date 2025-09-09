package br.edu.ufape.plataforma.mentoria.controller;

import br.edu.ufape.plataforma.mentoria.dto.MaterialDTO;
import br.edu.ufape.plataforma.mentoria.enums.InterestArea;
import br.edu.ufape.plataforma.mentoria.enums.MaterialType; // Importe o Enum
import br.edu.ufape.plataforma.mentoria.exceptions.EntityNotFoundException;
import br.edu.ufape.plataforma.mentoria.service.AuthService;
import br.edu.ufape.plataforma.mentoria.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final AuthService authService;
    private final MaterialService materialService;

    public MaterialController(MaterialService materialService, AuthService authService) {
        this.materialService = materialService;
        this.authService = authService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialDTO> createMaterial(
            @RequestParam("title") String title,
            @RequestParam("materialType") MaterialType materialType, 
            @RequestParam(value = "url", required = false) String url,
            @RequestParam("interestArea") Set<InterestArea> interestArea, 
            @RequestPart(name = "arquivo", required = false) MultipartFile arquivo) {
        try {
            Long userId = authService.getCurrentUser().getId();
            
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setTitle(title);
            materialDTO.setMaterialType(materialType);
            materialDTO.setUrl(url);
            materialDTO.setInterestArea(interestArea);

            MaterialDTO savedMaterialDTO = materialService.createMaterial(materialDTO, arquivo, userId);
            return new ResponseEntity<>(savedMaterialDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> getMaterialById(@PathVariable Long id) {
        try {
            MaterialDTO material = materialService.getMaterialById(id);
            return ResponseEntity.ok(material);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> getAllMaterial() {
        List<MaterialDTO> materiais = materialService.listAll();
        return ResponseEntity.ok(materiais);
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialDTO> updateMaterialById(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("materialType") MaterialType materialType, // Alterado para o Enum
            @RequestParam(value = "url", required = false) String url,
            @RequestParam("interestArea") Set<InterestArea> interestArea, // Mantido como Set<Enum>
            @RequestPart(name = "arquivo", required = false) MultipartFile arquivo) {
        try {
            MaterialDTO materialDTO = new MaterialDTO();
            materialDTO.setTitle(title);
            materialDTO.setMaterialType(materialType);
            materialDTO.setUrl(url);
            materialDTO.setInterestArea(interestArea);

            MaterialDTO updatedMaterialDTO = materialService.updateById(id, materialDTO, arquivo);
            return ResponseEntity.ok(updatedMaterialDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterialById(@PathVariable Long id) {
        try {
            materialService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sugestoes")
    public ResponseEntity<List<MaterialDTO>> suggestMaterials() {
        try {
            Long userId = authService.getCurrentUser().getId();
            List<MaterialDTO> sugestoes = materialService.suggestMaterials(userId);
            return ResponseEntity.ok(sugestoes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/filtrar-por-areas")
    public ResponseEntity<List<MaterialDTO>> filterByAreas(@RequestBody List<InterestArea> areas) {
        try {
            List<MaterialDTO> materiais = materialService.filterByInterestArea(areas);
            return ResponseEntity.ok(materiais);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}