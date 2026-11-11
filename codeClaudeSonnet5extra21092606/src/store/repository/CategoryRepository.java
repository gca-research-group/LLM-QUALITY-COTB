package store.repository;

import store.model.Category;

public class CategoryRepository extends InMemoryRepository<Category> {

    public CategoryRepository() {
        super("Category");
    }

    @Override
    protected String extractId(Category entity) {
        return entity.getId();
    }
}