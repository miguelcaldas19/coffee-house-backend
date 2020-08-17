package com.coffeehouse.stockmanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.coffeehouse.stockmanagement.entity.Category;
import com.coffeehouse.stockmanagement.entity.Role;
import com.coffeehouse.stockmanagement.enums.ERole;
import com.coffeehouse.stockmanagement.repository.CategoryRepository;
import com.coffeehouse.stockmanagement.repository.RoleRepository;
import com.coffeehouse.stockmanagement.utils.Constants;

@SpringBootApplication
public class StockManagementApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(StockManagementApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      return builder.sources(StockManagementApplication.class);
	}
	
	@Bean
    CommandLineRunner initRoles (RoleRepository roleRepository){
		
		return args -> {

        	if (!roleRepository.existsByName(ERole.MANAGER)) {
    			Role roleManager = new Role();
        		roleManager.setName(ERole.MANAGER);
        		roleRepository.save(roleManager);
    		}
    		
    		if (!roleRepository.existsByName(ERole.EMPLOYEE)) {
    			Role roleEmployee = new Role();
        		roleEmployee.setName(ERole.EMPLOYEE);
        		roleRepository.save(roleEmployee);
    		}
    	
        };
    }
	
	@Bean
    CommandLineRunner initCategories (CategoryRepository categoryRepository){
		
		return args -> {
		
			if (!categoryRepository.existsByName(Constants.CATEGORY_TYPE_COFFEE)) {
				Category categoryCoffee = new Category();
				categoryCoffee.setName(Constants.CATEGORY_TYPE_COFFEE);
				categoryRepository.save(categoryCoffee);
			}
			
			if (!categoryRepository.existsByName(Constants.CATEGORY_TYPE_TEA)) {
				Category categoryTea = new Category();
				categoryTea.setName(Constants.CATEGORY_TYPE_TEA);
				categoryRepository.save(categoryTea);
			}
			
			if (!categoryRepository.existsByName(Constants.CATEGORY_TYPE_OTHER)) {
				Category categoryOther = new Category();
				categoryOther.setName(Constants.CATEGORY_TYPE_OTHER);
				categoryRepository.save(categoryOther);
			}
			
		};
	}
	
}
