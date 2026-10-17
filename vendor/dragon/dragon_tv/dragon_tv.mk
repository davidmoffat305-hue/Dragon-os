# Dragon SO product definition.
# VERIFY: path of the Android TV base product on your AOSP branch.
$(call inherit-product, device/google/atv/products/atv_base.mk)

PRODUCT_NAME := dragon_tv
PRODUCT_DEVICE := dragon_tv
PRODUCT_BRAND := Dragon
PRODUCT_MODEL := Dragon SO TV
PRODUCT_MANUFACTURER := Dragon

# The Dragon home screen replaces the stock TV launcher.
PRODUCT_PACKAGES += DragonLauncher

PRODUCT_SYSTEM_PROPERTIES += \
    ro.dragonso.version=1.0 \
    ro.dragonso.codename=Hatchling
