package be.iccbxl.pid.reservationsSpringboot.controller;

import be.iccbxl.pid.reservationsSpringboot.model.Artist;
import be.iccbxl.pid.reservationsSpringboot.model.User;
import be.iccbxl.pid.reservationsSpringboot.service.ArtistService;
import be.iccbxl.pid.reservationsSpringboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class ArtistController {

    @Autowired
    ArtistService service;

    @Autowired
    UserService userService;

    @GetMapping("/artists")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByLogin(username);

        List<Artist> artists = service.getAllArtists();

        model.addAttribute("artists", artists);
        model.addAttribute("role", user.getRole());
        model.addAttribute("title", "Liste des artistes");

        return "artist/index";
    }

    @GetMapping("/artists/{id}")
    public String show(Model model, @PathVariable("id") long id, Principal principal) {
        Artist artist = service.getArtist(id);
        String username = principal.getName();
        User user = userService.findByLogin(username);
        model.addAttribute("artist", artist);
        model.addAttribute("role", user.getRole());
        model.addAttribute("title", "Fiche d'un artiste");

        return "artist/show";
    }
    @GetMapping("/artists/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id, HttpServletRequest request,Principal principal) {
        Artist artist = service.getArtist(id);
        String username = principal.getName();
        User user = userService.findByLogin(username);
        model.addAttribute("artist", artist);
        model.addAttribute("role", user.getRole());

        //Générer le lien retour pour l'annulation
        String referrer = request.getHeader("Referer");

        if(referrer!=null && !referrer.equals("")) {
            model.addAttribute("back", referrer);
        } else {
            model.addAttribute("back", "/artists/"+artist.getId());
        }

        return "artist/edit";
    }

    @PutMapping("/artists/{id}/edit")
    public String update(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, @PathVariable("id") long id, Model model) {

        if (bindingResult.hasErrors()) {
            return "artist/edit";
        }

        Artist existing = service.getArtist(id);

        if(existing==null) {
            return "artist/index";
        }

        service.updateArtist(id, artist);

        return "redirect:/artists/"+artist.getId();
    }
    @GetMapping("/artists/create")
    public String create(Model model) {
        Artist artist = new Artist(null,null);

        model.addAttribute("artist", artist);

        return "artist/create";
    }

    @PostMapping("/artists/create")
    public String store(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "artist/create";
        }

        service.addArtist(artist);
        return "redirect:/artists/"+artist.getId();
    }
    @DeleteMapping("/artists/{id}")
    public String delete(@PathVariable("id") long id, Model model) {
        Artist existing = service.getArtist(id);

        if(existing!=null) {
            service.deleteArtist(id);
        }

        return "redirect:/artists";
    }


}
