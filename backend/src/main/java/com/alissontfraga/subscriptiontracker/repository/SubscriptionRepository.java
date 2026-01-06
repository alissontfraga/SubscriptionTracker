package com.alissontfraga.subscriptiontracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alissontfraga.subscriptiontracker.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByOwner_Id(Long ownerId);

}
