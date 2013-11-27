package htnn;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

public class StorageManager<E> {

	private final Class<E> c;

	private static Set<String> classes = new HashSet<String>();

	public StorageManager(Class<E> c) {
		this.c = c;
		String classId = c.getPackage() + c.getName();

		if (!classes.contains(classId)) {
			synchronized (classes) {
				if (!classes.contains(classId)) {
					ObjectifyService.register(c);
					classes.add(classId);
				}
			}
		}

	}

	public E save(E e) {
		ofy().save().entity(e).now();
		return e;
	}

	public void saveAll(Iterable<? extends E> list) {
		ofy().save().entities(list).now();
	}

	public E get(Long id) {
		try {
			return ofy().load().type(c).id(id).get();
		} catch (NotFoundException e) {
			return null;
		}
	}

	public E get(String id) {
		try {
			return ofy().load().type(c).id(id).get();
		} catch (NotFoundException e) {
			return null;
		}
	}

	public Map<Key<E>, E> get(Iterable<Key<E>> keys) {
		try {
			return ofy().load().keys(keys);
		} catch (NotFoundException e) {
			return null;
		}
	}

	public void delete(E e) {
		ofy().delete().entity(e).now();
	}

	public void deleteAll(Iterable<E> e) {
		ofy().delete().entities(e).now();
	}

	public List<E> getAll() {
		return ofy().load().type(c).list();
	}

	public List<E> getBy(String order, int limit) {
		return ofy().load().type(c).order(order).limit(limit).list();
	}

	public List<E> getBy(String byString, Object byObject) {
		return ofy().load().type(c).filter(byString, byObject).list();
	}

	public E getSingleBy(String byString, Object byObject, String order) {
		return ofy().load().type(c).filter(byString, byObject).order(order)
				.first().get();
	}
	
	public E getSingleBy(String byString, Object byObject) {
		List<E> list = getBy(byString, byObject);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Query<E> filter(String byString, Object byObject) {
		Query<E> query = ofy().load().type(c).filter(byString, byObject);
		return query;
	}

	public List<E> getBy(String filterColumns[], Object filterValues[],
			String order, int limit) {
		Query<E> query = ofy().load().type(c);
		for (int i = 0; i < filterColumns.length; i++) {
			query = query.filter(filterColumns[i], filterValues[i]);
		}
		return query.order(order).limit(limit).list();
	}

	public List<E> getBy(String[] filterColumns, Object[] filterValues) {
		Query<E> query = ofy().load().type(c);
		for (int i = 0; i < filterColumns.length; i++) {
			query = query.filter(filterColumns[i], filterValues[i]);
		}
		return query.list();
	}
}
