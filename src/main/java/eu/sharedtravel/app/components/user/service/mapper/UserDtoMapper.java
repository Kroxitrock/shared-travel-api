package eu.sharedtravel.app.components.user.service.mapper;

import eu.sharedtravel.app.components.user.model.User;
import eu.sharedtravel.app.components.user.service.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDto userToUserDto(User entity);

}
