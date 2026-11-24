package com.market.backend_market.controllers;

import com.market.backend_market.magazynOfert.Offer_Repo;
import com.market.backend_market.models.Offer;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class Offers_Controller {

    private final Offer_Repo offer_Repo;

    public Offers_Controller(Offer_Repo offer_Repo) {
        this.offer_Repo = offer_Repo;

    }

    @GetMapping("/findById")
    public Offer findById(@RequestParam Long id) {
        if (offer_Repo.findById(id).isPresent()) {
            return offer_Repo.findById(id).get();
        }else {
            return null;
        }
    }

    @GetMapping("/findByName")
    public List<Offer> findByName(@RequestParam String name) {
        return offer_Repo.findByTitle(name);
    }

    @GetMapping("/findByCategory")
    public List<Offer> findByCategory(@RequestParam String category) {
        return offer_Repo.findByCategory(category);
    }

    @GetMapping("/findByUserId")
    public List<Offer> findByUserId(@RequestParam int userId) {
        return offer_Repo.findByUserId(userId);
    }

    @PostMapping("/addOffer")
    public Boolean addOffer(@RequestBody Offer offer, @RequestParam Authentication authentication) {
        if (offer_Repo.newOffer(offer, authentication)){
            return true;
        }else {
            return false;
        }
    }

    @PutMapping("/updateOffer")
    public Boolean updateOffer(@RequestBody Offer offer, @RequestParam Authentication authentication) {
        if (offer_Repo.editOffer(offer, authentication)){
            return true;
        }else {
            return false;
        }
    }

    @DeleteMapping("/deleteOffer/{id}")
    public Boolean deleteOffer(@PathVariable Long id, @RequestParam Authentication authentication) {
        if (offer_Repo.deleteOffer(id, authentication)){
            return true;
        }else {
            return false;
        }
    }
    @GetMapping
    public List<Offer> getOffers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return offer_Repo.searchOffers(q, category, minPrice, maxPrice, userId, sortBy, direction, page, size);
    }



}
