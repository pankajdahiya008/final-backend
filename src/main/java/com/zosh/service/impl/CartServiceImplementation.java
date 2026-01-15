package com.zosh.service.impl;

import com.zosh.exception.ProductException;
import com.zosh.model.Cart;
import com.zosh.model.CartItem;
import com.zosh.model.Product;
import com.zosh.model.User;
import com.zosh.repository.CartItemRepository;
import com.zosh.repository.CartRepository;
import com.zosh.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImplementation implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	/**
	 * Always return a cart.
	 * If cart does not exist, create it.
	 */
	@Override
	public Cart findUserCart(User user) {

		Cart cart = cartRepository.findByUserId(user.getId());

		// ✅ CREATE CART IF NOT EXISTS
		if (cart == null) {
			cart = new Cart();
			cart.setUser(user);
			cart.setTotalItem(0);
			cart.setTotalMrpPrice(0);
			cart.setTotalSellingPrice(0);
			cart.setDiscount(0);
			cart.setCouponPrice(0);
			cart = cartRepository.save(cart);
		}

		recalculateCartTotals(cart);

		return cart;
	}

	/**
	 * Add item to cart (cart is guaranteed to exist)
	 */
	@Override
	public CartItem addCartItem(
			User user,
			Product product,
			String size,
			int quantity
	) throws ProductException {

		Cart cart = findUserCart(user);

		CartItem existingItem =
				cartItemRepository.findByCartAndProductAndSize(cart, product, size);

		if (existingItem != null) {
			// Optional: update quantity instead of returning
			existingItem.setQuantity(existingItem.getQuantity() + quantity);
			existingItem.setMrpPrice(existingItem.getQuantity() * product.getMrpPrice());
			existingItem.setSellingPrice(existingItem.getQuantity() * product.getSellingPrice());
			return cartItemRepository.save(existingItem);
		}

		CartItem cartItem = new CartItem();
		cartItem.setCart(cart);
		cartItem.setProduct(product);
		cartItem.setSize(size);
		cartItem.setQuantity(quantity);
		cartItem.setUserId(user.getId());
		cartItem.setMrpPrice(quantity * product.getMrpPrice());
		cartItem.setSellingPrice(quantity * product.getSellingPrice());

		cart.getCartItems().add(cartItem);

		return cartItemRepository.save(cartItem);
	}

	@Override
	@Transactional
	public void clearCart(User user) {

		Cart cart = cartRepository.findByUserId(user.getId());

		if (cart == null) {
			return;
		}

		// ✅ HARD DELETE FROM DB (NO STALE STATE)
		cartItemRepository.deleteByCartId(cart.getId());

		// ✅ CLEAR IN-MEMORY STATE
		cart.getCartItems().clear();

		cart.setCouponCode(null);
		cart.setCouponPrice(0);
		cart.setTotalItem(0);
		cart.setTotalMrpPrice(0);
		cart.setTotalSellingPrice(0);
		cart.setDiscount(0);

		cartRepository.save(cart);
	}

	/**
	 * Recalculate totals from cart items
	 */
	private void recalculateCartTotals(Cart cart) {

		int totalMrp = 0;
		int totalSelling = 0;
		int totalQuantity = 0;

		for (CartItem item : cart.getCartItems()) {
			totalMrp += item.getMrpPrice();
			totalSelling += item.getSellingPrice();
			totalQuantity += item.getQuantity();
		}

		cart.setTotalMrpPrice(totalMrp);
		cart.setTotalSellingPrice(totalSelling - cart.getCouponPrice());
		cart.setTotalItem(totalQuantity);
		cart.setDiscount(calculateDiscountPercentage(totalMrp, totalSelling));

		cartRepository.save(cart);
	}

	public static int calculateDiscountPercentage(double mrpPrice, double sellingPrice) {
		if (mrpPrice <= 0) return 0;
		return (int) (((mrpPrice - sellingPrice) / mrpPrice) * 100);
	}
}