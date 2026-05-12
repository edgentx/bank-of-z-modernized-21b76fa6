package com.example.infrastructure.mongo.support;

import java.lang.reflect.Field;

/**
 * Reflection helper for the Mongo adapters in S-28.
 *
 * The domain aggregates expose getters for most of their state but their
 * constructors are id-only — every other field is mutated through {@code execute(cmd)}.
 * The persistence layer needs to (a) read enough state out of the aggregate to
 * write a {@code @Document}, and (b) restore that state back into a fresh
 * aggregate instance on load. Replaying the command history would couple the
 * adapters to every command type in the codebase; using reflection here keeps
 * the persistence concern in the infrastructure module and leaves the
 * domain shape intact.
 */
public final class AggregateFieldAccess {
  private AggregateFieldAccess() {}

  /** Set a private field on {@code target} (or any superclass). */
  public static void set(Object target, String fieldName, Object value) {
    try {
      Field f = findField(target.getClass(), fieldName);
      f.setAccessible(true);
      f.set(target, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException(
          "Failed to set field " + target.getClass().getSimpleName() + "." + fieldName, e);
    }
  }

  /** Read a private field on {@code target} (or any superclass). */
  public static Object get(Object target, String fieldName) {
    try {
      Field f = findField(target.getClass(), fieldName);
      f.setAccessible(true);
      return f.get(target);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException(
          "Failed to read field " + target.getClass().getSimpleName() + "." + fieldName, e);
    }
  }

  private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
    Class<?> cursor = clazz;
    while (cursor != null && cursor != Object.class) {
      try {
        return cursor.getDeclaredField(name);
      } catch (NoSuchFieldException ignored) {
        cursor = cursor.getSuperclass();
      }
    }
    throw new NoSuchFieldException(clazz.getName() + "." + name);
  }
}
