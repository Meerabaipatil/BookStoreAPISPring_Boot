package org.demo.FileHandling;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.demo.Entity.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class BookSpecification {

	public static Specification<Book> getSpecification(Integer id, String title, String author, Integer price,
			LocalDate publishesAt) {
		return new Specification<Book>() {

			@Override
			public @Nullable Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				List<Predicate> list = new ArrayList<>();

				if (id != null) {

					list.add(criteriaBuilder.equal(root.get("id"), id));
				}
				if (title != null && !title.isBlank()) {

					list.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
							"%" + title.trim().toLowerCase() + "%"));
				}

				if (author != null && !author.isEmpty()) {
					list.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")),
							"%" + author.trim().toLowerCase() + "%"));

				}

				if (price != null) {

					list.add(criteriaBuilder.equal(root.get("price"), price));
				}

				if (publishesAt != null) {

					list.add(criteriaBuilder.equal(root.get("publishesAt"), publishesAt));
				}
				

				return criteriaBuilder.and(list.toArray(new Predicate[0]));

			}
		};

	}
}
