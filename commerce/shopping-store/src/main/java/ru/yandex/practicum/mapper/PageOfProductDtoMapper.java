package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.common.model.PageOfProductDto;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.SortDto;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.common.model.SortDto.DirectionEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер, превращающий Page<Product> в PageOfProductDto
 * с учетом сортировки (List<SortDto>).
 *
 * Используем абстрактный класс, чтобы MapStruct
 * мог сгенерировать bean и внедрить (autowire) нам другие мапперы.
 */
@Mapper(componentModel = "spring")
public abstract class PageOfProductDtoMapper {

    @Autowired
    protected ProductMapper productMapper;

    /**
     * Основной метод, который на вход получает Spring Data Page<Product>,
     * а на выход отдаёт PageOfProductDto.
     */
    public PageOfProductDto toPageDto(Page<Product> pageResult) {
        // 1) Создаём PageOfProductDto
        PageOfProductDto dto = new PageOfProductDto();

        // 2) Конвертируем content (List<Product>) -> List<ProductDto>
        List<ProductDto> content = pageResult.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
        dto.setContent(content);

        // 3) Заполняем поля пагинации
        dto.setNumber(pageResult.getNumber());
        dto.setSize(pageResult.getSize());
        dto.setTotalElements(pageResult.getTotalElements());
        dto.setTotalPages(pageResult.getTotalPages());
        dto.setNumberOfElements(pageResult.getNumberOfElements());
        dto.setFirst(pageResult.isFirst());
        dto.setLast(pageResult.isLast());
        dto.setEmpty(pageResult.isEmpty());

        // 4) Заполняем поле sort (List<SortDto>)
        //    pageResult.getSort() -> Iterable<Order>
        //    Преобразуем каждый Order -> SortDto
        List<SortDto> sortDtoList = pageResult.getSort().stream()
                .map(order -> {
                    SortDto s = new SortDto();
                    s.setProperty(order.getProperty());
                    s.setDirection(order.isAscending() ? DirectionEnum.ASC : DirectionEnum.DESC);
                    return s;
                })
                .collect(Collectors.toList());

        dto.setSort(sortDtoList);

        return dto;
    }
}
