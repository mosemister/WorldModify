package org.world.modify.shapes;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Shapes {

	Cube CUBE = new Cube();
	Line LINE = new Line();

	static Collection<Shape> getShapes() {
		return Arrays
				.stream(Shapes.class.getFields())
				.filter(field -> Modifier.isFinal(field.getModifiers()))
				.filter(field -> Modifier.isPublic(field.getModifiers()))
				.filter(field -> Modifier.isStatic(field.getModifiers()))
				.filter(field -> Shape.class.isAssignableFrom(field.getType()))
				.map(field -> {
					try {
						return (Shape) field.get(null);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
