package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    // Request path: /menu
    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    //Request path: /menu/add
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";

    }

    //Request path: /menu/add
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

    //Request path: /menu/view/{menu.id}
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable("id") int id, Model model) {

        model.addAttribute("menu", menuDao.findOne(id));
        return "menu/view";

    }

    @RequestMapping(value = "/add-item/{id}", method = RequestMethod.GET)
    public String addItem(@PathVariable("id") int id, Model model) {
        Menu menu = menuDao.findOne(id);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to the menu: " + menu.getName());
        model.addAttribute("currentId", id);

        return "menu/add-item";

    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(@ModelAttribute @Valid AddMenuItemForm newForm, Errors errors,
                          Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Error!: " + newForm.getMenuId());
            model.addAttribute("form", newForm);

            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(newForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(newForm.getCheeseId());
        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:/menu/view/" + menu.getId();

    }
}
