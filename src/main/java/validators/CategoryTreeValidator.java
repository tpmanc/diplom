package validators;

import models.CategoryTreeModel;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Валидация модели CategoryTreeModel
 */
public class CategoryTreeValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryTreeModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CategoryTreeModel model = (CategoryTreeModel) o;

        // parent
//        if (model.getParent() == null) {
//            errors.reject("parent", "Необходимо указать родителя");
//        }
//        if (model.getParent().length() > 255) {
//            errors.reject("parent", "parent должно быть меньше 255 символов");
//        }

        // treeId
//        if (model.getTreeId() == null) {
//            errors.reject("treeId", "Необходимо указать id элемента в дереве");
//        }
//        if (model.getTreeId().length() > 255) {
//            errors.reject("treeId", "treeId должно быть меньше 255 символов");
//        }
//
//        // title
//        if (model.getTitle().length() > 255) {
//            errors.reject("title", "title должно быть меньше 255 символов");
//        }
//
//        // categoryId & title
//        if (model.getCategoryId() == null && model.getTitle() == null) {
//            errors.reject("categoryId & title", "Должно быть заполнено одно из полей: categoryId, title");
//        }
    }
}
