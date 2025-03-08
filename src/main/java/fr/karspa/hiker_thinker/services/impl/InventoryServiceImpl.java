package fr.karspa.hiker_thinker.services.impl;

import com.mongodb.client.result.UpdateResult;
import fr.karspa.hiker_thinker.dtos.EquipmentDTO;
import fr.karspa.hiker_thinker.dtos.responses.InventoryDTO;
import fr.karspa.hiker_thinker.model.Equipment;
import fr.karspa.hiker_thinker.model.EquipmentCategory;
import fr.karspa.hiker_thinker.repository.InventoryRepository;
import fr.karspa.hiker_thinker.services.InventoryService;
import fr.karspa.hiker_thinker.utils.ResponseModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public ResponseModel<InventoryDTO> findByUserId(String userId) {

        //Récupérer l'inventaire dans la BDD
        var inventory = inventoryRepository.getInventory(userId);

        //Check si null
        if(inventory == null)
            return ResponseModel.buildResponse("710", "Aucun inventaire disponible.", null);


        // => Grouper les équipements par catégories et retourner le tableau associatif généré.
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setEquipments(inventory);

        return ResponseModel.buildResponse("200", "Inventaire récupéré avec succès.", inventoryDTO);
    }

    @Override
    public ResponseModel<Equipment> addEquipment(String userId, EquipmentDTO equipmentDTO) {

        // Créer les instance nécessaires aux vérifications et enregistrement.
        Equipment equipment = equipmentDTO.mapToEntity();
        String categoryName = equipmentDTO.getCategoryName();


        //Vérifier l'unicité du name avant d'enregistrer
        boolean isNameAvailable = this.checkAvailableEquipmentName(userId, equipment);

        if(!isNameAvailable){
            return ResponseModel.buildResponse("400", "Un équipement avec ce nom existe déjà. ("+equipment.getName()+")", null);
        }

        //Générer un nouvel identifiant unique pour cet équipement
        String uniqueId = UUID.randomUUID().toString();
        equipment.setId(uniqueId);


        boolean doesCategoryExist = inventoryRepository.checkCategoryExistsByName(userId, categoryName);

        // Si la catégorie n'existe pas, l'ajouter dans la liste des catégories de l'inventaire
        if (!doesCategoryExist) {

            String categoryId = UUID.randomUUID().toString();
            EquipmentCategory newCategory = new EquipmentCategory();
            newCategory.setId(categoryId);
            newCategory.setName(categoryName);
            // icon null et on affichera un truc générique

            // Ne pas oublier d'ajouter la référence à la nouvelle catégorie dans l'équipement.
            equipment.setCategoryId(categoryId);

            UpdateResult resultCat = inventoryRepository.addCategoryToCategoryList(userId, newCategory);
            // Si aucune catégorie n'a été ajoutée, retourner une erreur
            if (resultCat.getModifiedCount() == 0) {
                return ResponseModel.buildResponse("500", "Échec de l'ajout de la catégorie.", null);
            }
        }

        // Ajouter l'équipement à la liste des équipements de l'inventaire
        UpdateResult resultEquip = inventoryRepository.addEquipmentToEquipmentList(userId, equipment);

        if (resultEquip.getModifiedCount() > 0) {
            return ResponseModel.buildResponse("201", "Équipement ajouté avec succès.", equipment);
        } else {
            return ResponseModel.buildResponse("500", "Échec de l'ajout de l'équipement.", null);
        }
    }

    @Override
    public ResponseModel<Equipment> modifyEquipment(String userId, Equipment equipment) {

        //Vérifier que l'équipement avec cet id existe dans l'inventaire.
        boolean doesIdExists = this.checkEquipmentExistsById(userId, equipment);

        if(!doesIdExists){
            return ResponseModel.buildResponse("404", "Aucun équipement avec cet identifiant n'existe pour cette catégorie.", null);
        }

        //TODO : => VALIDER LES DONNÉES EN ENTRÉE ET S'ASSURER QU'IL Y AI BIEN UN ID.

        //On appelle la même méthode que pour l'ajout mais les requêtes de vérifications ne sont pas les mêmes si un id à l'équipement est passé ou non.
        boolean isNameAvailable = this.checkAvailableEquipmentName(userId, equipment);
        if(!isNameAvailable){
            return ResponseModel.buildResponse("409", "Un équipement avec ce nom existe déjà dans votre inventaire. ("+equipment.getName()+")", null);
        }

        // Vérifier que la nouvelle catégorie existe bien dans inventory.categories
        boolean doesCategoryExist = inventoryRepository.checkCategoryExistsByName(userId, equipment.getCategoryId());
        if(!doesCategoryExist){
            return ResponseModel.buildResponse("400", "La catégorie de l'équipement n'existe pas dans votre inventaire. Veuillez la créée avant.", null);
        }

        //Modifier l'équipement avec ce qui est passé dans la requête.
        UpdateResult result = inventoryRepository.modifyEquipment(userId, equipment);

        if (result.getMatchedCount() > 0) {
            return ResponseModel.buildResponse("200", "Équipement modifié avec succès.", equipment);
        } else {
            return ResponseModel.buildResponse("404", "Erreur bizarre.", null);
        }

    }


    @Override
    public ResponseModel<Equipment> removeEquipment(String userId, String equipmentId) {

        // Supprimer l'élément (en utilisant l'equipmentId passé en paramètre).
        UpdateResult result = inventoryRepository.removeEquipment(userId, equipmentId);

        if (result.getMatchedCount() > 0) {
            return ResponseModel.buildResponse("204", "Équipement supprimé avec succès.", null);
        } else {
            return ResponseModel.buildResponse("404", "Erreur bizarre.", null);
        }
    }


    @Override
    public ResponseModel<EquipmentCategory> addCategory(String userId, EquipmentCategory category){

        boolean doesCategoryExist = inventoryRepository.checkCategoryExistsByName(userId, category.getName());
        if(doesCategoryExist){
            return ResponseModel.buildResponse("400", "La catégorie existe déjà dans l'inventaire.", null);
        }

        UpdateResult result = inventoryRepository.addCategoryToCategoryList(userId, category);

        if (result.getMatchedCount() > 0) {
            return ResponseModel.buildResponse("201", "Catégorie créée avec succès.", category);
        } else {
            return ResponseModel.buildResponse("404", "Erreur bizarre.", null);
        }
    }


    private boolean checkEquipmentExistsById(String userId, Equipment equipment){
        return inventoryRepository.checkEquipmentExistsById(userId, equipment);
    }


    private boolean checkAvailableEquipmentName(String userId, Equipment equipment){
        return inventoryRepository.checkAvailableEquipmentName(userId, equipment);
    }
}
