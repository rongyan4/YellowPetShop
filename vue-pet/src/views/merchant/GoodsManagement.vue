<template>
  <div class="goods-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="11" cy="11" r="8"></circle>
          <path d="m21 21-4.35-4.35"></path>
        </svg>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索商品名称..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>
      <button @click="showAddDialog = true" class="add-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        添加商品
      </button>
    </div>

    <!-- 商品列表 -->
    <div class="goods-table-wrapper">
      <table class="goods-table">
        <thead>
          <tr>
            <th>商品ID</th>
            <th>商品名称</th>
            <th>分类</th>
            <th>价格</th>
            <th>销量</th>
            <th>状态</th>
            <th style="width: 200px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in goodsList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>
              <div class="goods-info">
                <img :src="item.mainPicUrl || '/images/goods/ml.png'" :alt="item.name" class="goods-image" />
                <span>{{ item.name }}</span>
              </div>
            </td>
            <td>{{ item.categoryName || '-' }}</td>
            <td class="price">¥{{ item.price }}</td>
            <td>{{ item.sold || 0 }}</td>
            <td>
              <span 
                class="status-badge" 
                :class="item.isValid ? 'active' : 'inactive'"
                @click="handleToggleStatus(item)"
                style="cursor: pointer;"
              >
                {{ item.isValid ? '上架' : '下架' }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <button @click="handleEdit(item)" class="btn-edit">编辑</button>
                <button @click="handleViewComments(item)" class="btn-comment">评论</button>
                <button @click="handleDelete(item.id)" class="btn-delete">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="goodsList.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无商品数据</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">
        下一页
      </button>
    </div>

    <!-- 添加/编辑对话框 -->
    <van-popup v-model:show="showAddDialog" position="center" :style="{ width: '90%', maxWidth: '600px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingGoods ? '编辑商品' : '添加商品' }}</h3>
          <button @click="closeDialog" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>商品名称</label>
            <input v-model="formData.name" type="text" placeholder="请输入商品名称" />
          </div>
          <div class="form-group">
            <label>商品价格</label>
            <input v-model="formData.price" type="number" step="0.01" placeholder="请输入价格" />
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="formData.description" placeholder="请输入商品描述" rows="3"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="closeDialog" class="btn-cancel">取消</button>
          <button @click="handleSubmit" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { 
  updateProductStatus, 
  getProductComments, 
  getProductList,
  addProduct,
  updateProduct,
  deleteProduct 
} from '@/api/merchant';
import { showConfirmDialog, showSuccessToast, showFailToast, showToast } from 'vant';

const router = useRouter();

const searchKeyword = ref('');
const goodsList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(0);

const showAddDialog = ref(false);
const editingGoods = ref(null);
const formData = ref({
  name: '',
  price: '',
  stock: '',
  description: ''
});

const loadGoodsList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value
    };
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }
    
    const result = await getProductList(params);
    
    if (result && result.code === 200) {
      goodsList.value = result.data.records || [];
      total.value = result.data.total || 0;
      totalPages.value = result.data.pages || 0;
    } else {
      showFailToast(result.msg || '加载失败');
    }
  } catch (error) {
    console.error('加载商品列表失败:', error);
    showFailToast('加载失败');
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadGoodsList();
};

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadGoodsList();
  }
};

const handleEdit = (item) => {
  editingGoods.value = item;
  formData.value = {
    id: item.id,
    name: item.name,
    price: item.price,
    description: item.msg || ''
  };
  showAddDialog.value = true;
};

const handleDelete = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这个商品吗？',
    confirmButtonColor: '#ff6b6b'
  }).then(async () => {
    try {
      const result = await deleteProduct(id);
      if (result && result.code === 200) {
        showSuccessToast('删除成功');
        loadGoodsList();
      } else {
        showFailToast(result.msg || '删除失败');
      }
    } catch (error) {
      showFailToast('删除失败');
    }
  }).catch(() => {});
};

const handleToggleStatus = async (item) => {
  const newStatus = !item.isValid;
  const action = newStatus ? '上架' : '下架';
  
  try {
    const result = await updateProductStatus(item.id, newStatus);
    if (result && result.code === 200) {
      showSuccessToast(`${action}成功`);
      item.isValid = newStatus;
    } else {
      showFailToast(result.msg || `${action}失败`);
    }
  } catch (error) {
    showFailToast(`${action}失败`);
  }
};

const handleViewComments = (item) => {
  router.push(`/merchant/products/${item.id}/comments`);
};

const handleSubmit = async () => {
  if (!formData.value.name || !formData.value.price) {
    showFailToast('请填写完整信息');
    return;
  }

  try {
    const data = {
      name: formData.value.name,
      price: parseFloat(formData.value.price),
      msg: formData.value.description || ''
    };
    
    let result;
    if (editingGoods.value) {
      // 编辑商品
      result = await updateProduct(editingGoods.value.id, data);
    } else {
      // 添加商品
      result = await addProduct(data);
    }
    
    if (result && result.code === 200) {
      showSuccessToast(editingGoods.value ? '更新成功' : '添加成功');
      closeDialog();
      loadGoodsList();
    } else {
      showFailToast(result.msg || '操作失败');
    }
  } catch (error) {
    showFailToast('操作失败');
  }
};

const closeDialog = () => {
  showAddDialog.value = false;
  editingGoods.value = null;
  formData.value = {
    name: '',
    price: '',
    description: ''
  };
};

onMounted(() => {
  loadGoodsList();
});
</script>

<style scoped>
.goods-management {
  max-width: 1400px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  stroke-width: 2;
  color: #95a5a6;
}

.search-input {
  width: 100%;
  height: 44px;
  padding: 0 16px 0 48px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.search-input:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 24px;
  height: 44px;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(152, 216, 200, 0.3);
}

.add-btn svg {
  width: 18px;
  height: 18px;
  stroke-width: 2.5;
}

.goods-table-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.goods-table {
  width: 100%;
  border-collapse: collapse;
}

.goods-table thead {
  background: #f8f9fa;
}

.goods-table th {
  padding: 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
}

.goods-table td {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #636e72;
}

.goods-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.price {
  color: #98D8C8;
  font-weight: 600;
  font-size: 15px;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.status-badge:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.active:hover {
  background: #c3e6cb;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.inactive:hover {
  background: #f5c6cb;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-edit,
.btn-delete,
.btn-comment {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-edit:hover {
  background: #bbdefb;
}

.btn-comment {
  background: #e1bee7;
  color: #6a1b9a;
}

.btn-comment:hover {
  background: #ce93d8;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #95a5a6;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  stroke-width: 1.5;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 20px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #98D8C8;
  color: #98D8C8;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #636e72;
}

/* 对话框样式 */
.dialog {
  padding: 24px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dialog-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  font-size: 24px;
  color: #636e72;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #e9ecef;
}

.dialog-body {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f8f9fa;
  color: #636e72;
}

.btn-cancel:hover {
  background: #e9ecef;
}

.btn-confirm {
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(152, 216, 200, 0.3);
}
</style>
