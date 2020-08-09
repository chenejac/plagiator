package com.ftn.plagiator.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperUtil {
	
	@Autowired
    private ModelMapper modelMapper;
    
    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param <D>      type of result object.
     * @param <T>      type of source object to map from.
     * @param object   object that needs to be mapped.
     * @param outClass class of result object.
     * @return new object of <code>outClass</code> type.
     */
    public <D, T> D map(final T object, Class<D> outClass) {
        return modelMapper.map(object, outClass);
    }
    
    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param objectList list of objects that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of object in <code>objectList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public <D, T> List<D> mapAll(final Collection<T> objectList, Class<D> outCLass) {
        return objectList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }
    
    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param objectList Page of objects that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of object in <code>objectList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public <D, T> List<D> mapAll(final Page<T> objectList, Class<D> outCLass) {
        return objectList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }
}
