const homeModel = require('../Models/homeModel');
const { success, asyncHandler } = require('../utils/response');

/**
 * 获取轮播图数据
 */
exports.getSwipe = asyncHandler(async (req, res) => {
  const data = homeModel.getSwipeData();
  success(res, data, '获取轮播图成功');
});

/**
 * 获取推荐商品数据
 */
exports.getRecommend = asyncHandler(async (req, res) => {
  const data = homeModel.getRecommendData();
  success(res, data, '获取推荐商品成功');
});