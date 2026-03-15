package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDesc(user);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getById(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền truy cập địa chỉ này");
        }
        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        if (request.isDefault()) {
            unsetDefaultForUser(user);
        }

        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse update(Long addressId, Long userId, AddressUpdateRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền cập nhật địa chỉ này");
        }

        addressMapper.updateFromRequest(request, address);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultForUser(address.getUser());
            address.setDefault(true);
        }

        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    @Transactional
    public void delete(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền xóa địa chỉ này");
        }
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefault(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền thay đổi địa chỉ này");
        }
        unsetDefaultForUser(address.getUser());
        address.setDefault(true);
        address = addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    private void unsetDefaultForUser(User user) {
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDesc(user);
        addresses.stream()
                .filter(Address::isDefault)
                .forEach(a -> a.setDefault(false));
    }
}
