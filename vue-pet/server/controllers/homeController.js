const homeModel = require('../models/homeModel');

exports.getSwipe = (req, res, next) => {
  try {
    const data = homeModel.getSwipeData();
    res.json({ 
      code: 200,
      data: data
    }); 
  } catch (error) { 
    res.json({
      code: 500,
      msg: '获取Swipe失败'
    });
  }
};