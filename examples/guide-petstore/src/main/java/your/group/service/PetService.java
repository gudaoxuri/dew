package your.group.service;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.service.CRUSService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import your.group.dao.PetDao;
import your.group.entity.Pet;

@Service
@CacheConfig(cacheNames = "petstore:pet") // 启用缓存指定key前缀
public class PetService implements CRUSService<PetDao, Integer, Pet> {

    @Override
    @Cacheable // 缓存id对应的内容
    public Resp<Pet> getById(Integer id) {
        return CRUSService.super.getById(id);
    }

    @Override
    @CacheEvict // 清除id对应的内容
    public Resp<Void> disableById(Integer id) {
        return CRUSService.super.disableById(id);
    }

    @Override
    @CachePut("#pet.id") // 添加/更新id对应的内容
    public Resp<Pet> save(Pet pet) {
        return CRUSService.super.save(pet);
    }

    @Override
    @CachePut("#id")// 添加/更新id对应的内容
    public Resp<Pet> updateById(Integer id, Pet pet) {
        return CRUSService.super.updateById(id, pet);
    }
}
