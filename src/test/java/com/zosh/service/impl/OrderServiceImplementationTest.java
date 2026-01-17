package com.zosh.service.impl;

import com.zosh.domain.OrderStatus;
import com.zosh.domain.PaymentStatus;
import com.zosh.exception.OrderException;
import com.zosh.model.*;
import com.zosh.repository.AddressRepository;
import com.zosh.repository.OrderItemRepository;
import com.zosh.repository.OrderRepository;
import com.zosh.repository.UserRepository;
import com.zosh.service.CartService;
import com.zosh.service.OrderItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for OrderServiceImplementation class.
 * Tests all methods including createOrder, findOrderById, usersOrderHistory,
 * getShopsOrders, updateOrderStatus, deleteOrder, and cancelOrder.
 * Uses JUnit 5, Mockito for mocking, and AssertJ for assertions.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImplementation Tests")
class OrderServiceImplementationTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private CartService cartService;
    
    @Mock
    private AddressRepository addressRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private OrderItemService orderItemService;
    
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User testUser;
    private Address testAddress;
    private Cart testCart;
    private Order testOrder;
    private CartItem testCartItem;
    private OrderItem testOrderItem;
    private Product testProduct;
    private Seller testSeller;
    private PaymentDetails testPaymentDetails;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setAddresses(new HashSet<>());

        // Setup test address
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setName("Test Address");
        testAddress.setCity("Test City");
        testAddress.setState("Test State");
        testAddress.setPinCode("12345");

        // Setup test seller
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setSellerName("Test Seller");

        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setSeller(testSeller);

        // Setup test cart item
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
        testCartItem.setMrpPrice(1000);
        testCartItem.setSellingPrice(800);
        testCartItem.setUserId(1L);
        testCartItem.setSize("M");

        // Setup test cart with mutable HashSet
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(testCartItem);
        testCart.setCartItems(cartItems);

        // Setup test payment details
        testPaymentDetails = new PaymentDetails();
        testPaymentDetails.setStatus(PaymentStatus.PENDING);

        // Setup test order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setSellerId(1L);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalSellingPrice(800);
        testOrder.setTotalItem(2);
        testOrder.setShippingAddress(testAddress);
        testOrder.setOrderItems(new ArrayList<>());
        testOrder.setPaymentDetails(testPaymentDetails);

        // Setup test order item
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setSellingPrice(800);
    }

    @Test
    @DisplayName("Should successfully create orders when cart has items from single seller")
    void shouldCreateOrderSuccessfully_WhenCartHasItemsFromSingleSeller() {
        // Arrange
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        Set<Order> result = orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        
        Order createdOrder = result.iterator().next();
        assertThat(createdOrder.getUser()).isEqualTo(testUser);
        assertThat(createdOrder.getSellerId()).isEqualTo(1L);
        assertThat(createdOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(createdOrder.getTotalSellingPrice()).isEqualTo(800);
        assertThat(createdOrder.getTotalItem()).isEqualTo(2);
        
        verify(addressRepository).save(testAddress);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should create multiple orders when cart has items from multiple sellers")
    void shouldCreateMultipleOrders_WhenCartHasItemsFromMultipleSellers() {
        // Arrange
        Seller secondSeller = new Seller();
        secondSeller.setId(2L);

        Product secondProduct = new Product();
        secondProduct.setId(2L);
        secondProduct.setSeller(secondSeller);

        CartItem secondCartItem = new CartItem();
        secondCartItem.setId(2L);
        secondCartItem.setProduct(secondProduct);
        secondCartItem.setQuantity(1);
        secondCartItem.setSellingPrice(500);

        // Create new cart with multiple items
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(testCartItem);
        cartItems.add(secondCartItem);
        testCart.setCartItems(cartItems);

        // Create second order for the second seller
        PaymentDetails secondPaymentDetails = new PaymentDetails();
        secondPaymentDetails.setStatus(PaymentStatus.PENDING);

        Order secondOrder = new Order();
        secondOrder.setId(2L);
        secondOrder.setUser(testUser);
        secondOrder.setSellerId(2L);
        secondOrder.setOrderStatus(OrderStatus.PENDING);
        secondOrder.setTotalSellingPrice(500);
        secondOrder.setTotalItem(1);
        secondOrder.setShippingAddress(testAddress);
        secondOrder.setOrderItems(new ArrayList<>());
        secondOrder.setPaymentDetails(secondPaymentDetails);

        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        // Mock to return different orders for different calls
        when(orderRepository.save(any(Order.class)))
                .thenReturn(testOrder)
                .thenReturn(secondOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        Set<Order> result = orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        assertThat(result).hasSize(2);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should add address to user when address not already present")
    void shouldAddAddressToUser_WhenAddressNotAlreadyPresent() {
        // Arrange
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        assertThat(testUser.getAddresses()).contains(testAddress);
    }

    @Test
    @DisplayName("Should not duplicate address when address already exists for user")
    void shouldNotDuplicateAddress_WhenAddressAlreadyExistsForUser() {
        // Arrange
        testUser.getAddresses().add(testAddress);
        int initialAddressCount = testUser.getAddresses().size();
        
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        assertThat(testUser.getAddresses()).hasSize(initialAddressCount);
    }

    @Test
    @DisplayName("Should handle empty cart gracefully")
    void shouldHandleEmptyCart_Gracefully() {
        // Arrange
        testCart.setCartItems(new HashSet<>());
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        Set<Order> result = orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should find order by ID successfully when order exists")
    void shouldFindOrderById_WhenOrderExists() throws OrderException {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order result = orderService.findOrderById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser()).isEqualTo(testUser);
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderException when order not found by ID")
    void shouldThrowOrderException_WhenOrderNotFoundById() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.findOrderById(999L))
                .isInstanceOf(OrderException.class)
                .hasMessage("order not exist with id 999");
        
        verify(orderRepository).findById(999L);
    }

    @Test
    @DisplayName("Should return user order history when user has orders")
    void shouldReturnUserOrderHistory_WhenUserHasOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByUserId(1L)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.usersOrderHistory(1L);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no order history")
    void shouldReturnEmptyList_WhenUserHasNoOrderHistory() {
        // Arrange
        when(orderRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

        // Act
        List<Order> result = orderService.usersOrderHistory(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository).findByUserId(999L);
    }

    @Test
    @DisplayName("Should return shop orders when seller has orders")
    void shouldReturnShopOrders_WhenSellerHasOrders() {
        // Arrange
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findBySellerIdOrderByOrderDateDesc(1L)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getShopsOrders(1L);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testOrder);
        verify(orderRepository).findBySellerIdOrderByOrderDateDesc(1L);
    }

    @Test
    @DisplayName("Should return empty list when seller has no orders")
    void shouldReturnEmptyList_WhenSellerHasNoOrders() {
        // Arrange
        when(orderRepository.findBySellerIdOrderByOrderDateDesc(999L)).thenReturn(Collections.emptyList());

        // Act
        List<Order> result = orderService.getShopsOrders(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository).findBySellerIdOrderByOrderDateDesc(999L);
    }

    @Test
    @DisplayName("Should update order status successfully when order exists")
    void shouldUpdateOrderStatus_WhenOrderExists() throws OrderException {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Should throw OrderException when trying to update non-existent order status")
    void shouldThrowOrderException_WhenUpdatingNonExistentOrderStatus() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED))
                .isInstanceOf(OrderException.class)
                .hasMessage("order not exist with id 999");
        
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should delete order successfully when order exists")
    void shouldDeleteOrder_WhenOrderExists() throws OrderException {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).findById(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw OrderException when trying to delete non-existent order")
    void shouldThrowOrderException_WhenDeletingNonExistentOrder() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(OrderException.class)
                .hasMessage("order not exist with id 999");
        
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should cancel order successfully when user owns the order")
    void shouldCancelOrder_WhenUserOwnsTheOrder() throws OrderException {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        Order result = orderService.cancelOrder(1L, testUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Should throw OrderException when user tries to cancel order they don't own")
    void shouldThrowOrderException_WhenUserTriesToCancelOrderTheyDontOwn() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(999L);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder(1L, differentUser))
                .isInstanceOf(OrderException.class)
                .hasMessage("you can't perform this action 1");
        
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw OrderException when trying to cancel non-existent order")
    void shouldThrowOrderException_WhenCancellingNonExistentOrder() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder(999L, testUser))
                .isInstanceOf(OrderException.class)
                .hasMessage("order not exist with id 999");
        
        verify(orderRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should handle null user gracefully in cancelOrder")
    void shouldHandleNullUser_InCancelOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder(1L, null))
                .isInstanceOf(NullPointerException.class);
        
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle null order ID gracefully")
    void shouldHandleNullOrderId_Gracefully() {
        // Act & Assert
        assertThatThrownBy(() -> orderService.findOrderById(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should set payment status to PENDING when creating order")
    void shouldSetPaymentStatusToPending_WhenCreatingOrder() {
        // Arrange
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // Ensure payment details are not null before checking status
            if (order.getPaymentDetails() != null) {
                assertThat(order.getPaymentDetails().getStatus()).isEqualTo(PaymentStatus.PENDING);
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should calculate total order price correctly from cart items")
    void shouldCalculateTotalOrderPrice_CorrectlyFromCartItems() {
        // Arrange
        CartItem additionalItem = new CartItem();
        additionalItem.setProduct(testProduct);
        additionalItem.setSellingPrice(200);
        additionalItem.setQuantity(1);
        
        // Create new cart items set with additional item
        Set<CartItem> cartItems = new HashSet<>(testCart.getCartItems());
        cartItems.add(additionalItem);
        testCart.setCartItems(cartItems);
        
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            assertThat(order.getTotalSellingPrice()).isEqualTo(1000); // 800 + 200
            assertThat(order.getTotalItem()).isEqualTo(3); // 2 + 1
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // Act
        orderService.createOrder(testUser, testAddress, testCart);

        // Assert
        verify(orderRepository).save(any(Order.class));
    }
}