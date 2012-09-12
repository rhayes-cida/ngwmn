package gov.usgs.ngwmn;

import org.springframework.beans.factory.FactoryBean;

public class NullFactoryBean<T> implements FactoryBean<T> {
    private final Class<?> objectType;

    public NullFactoryBean(Class<?> objectType) {
        this.objectType = objectType;
    }

    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}