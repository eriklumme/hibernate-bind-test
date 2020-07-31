package org.hibernate.bugs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	public void createEMF(Map properties) {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU", properties);
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	public void hhh14130_test_works() {
		createEMF(null);
		testEntityCanBeFoundWhereInIds();
	}

	@Test
	public void hhh14130_test_fails() {
		createEMF(Collections.singletonMap("hibernate.criteria.literal_handling_mode", "BIND"));
		testEntityCanBeFoundWhereInIds();
	}

	private void testEntityCanBeFoundWhereInIds() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		Long id = createEntityAndGetId(entityManager);
		List<TestEntity> entities = findById(entityManager, id);
		Assert.assertEquals(1, entities.size());

		entityManager.close();
	}

	private Long createEntityAndGetId(EntityManager entityManager) {
		entityManager.getTransaction().begin();

		TestEntity testEntity = new TestEntity();

		entityManager.persist(testEntity);
		entityManager.getTransaction().commit();

		return testEntity.getId();
	}

	private List<TestEntity> findById(EntityManager entityManager, Object... ids) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<TestEntity> cq = cb.createQuery(TestEntity.class);
		Root<TestEntity> root = cq.from(TestEntity.class);
		cq.where(root.in(ids));
		return entityManager.createQuery(cq).getResultList();
	}
}
