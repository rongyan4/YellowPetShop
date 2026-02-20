<template>
  <div class="category-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <button @click="showAddDialog = true" class="add-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        添加分类
      </button>
    </div>

    <!-- 分类列表 -->
    <div class="category-table-wrapper">
      <table class="category-table">
        <thead>
          <tr>
            <th>分类ID</th>
            <th>分类名称</th>
            <th>图标</th>
            <th>排序</th>
            <th>创建时间</th>
            <th style="width: 180px;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in categoryList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>
              <img v-if="item.icon" :src="item.icon" class="category-icon" />
              <span v-else>-</span>
            </td>
            <td>{{ item.sortOrder }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <div class="action-buttons">
                <button @click="handleEdit(item)" class="btn-edit">编辑</button>
                <button @click="handleDelete(item.id)" class="btn-delete">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="categoryList.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无分类数据</p>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <van-popup v-model:show="showAddDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingCategory ? '编辑分类' : '添加分类' }}</h3>
          <button @click="closeDialog" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>分类名称 <span class="required">*</span></label>
            <input v-model="formData.name" type="text" placeholder="请输入分类名称" />
          </div>
          <div class="form-group">
            <label>图标URL</label>
            <input v-model="formData.icon" type="text" placeholder="请输入图标URL（可选）" />
          </div>
          <div class="form-group">
            <label>排序顺序</label>
            <input v-model.number="formData.sortOrder" type="number" placeholder="数字越小越靠前" />
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
import { 
  getCategoryList,
  addCategory,
  updateCategory,
  deleteCategory 
} from '@/api/merchant';
import { showConfirmDialog, showSuccessToast, showFailToast } from 'vant';

const categoryList = ref([]);
const showAddDialog = ref(false);
const editingCategory = ref(null);
const formData = ref({
  name: '',
  icon: '',
  sortOrder: 0
});

const loadCategoryList = async () => {
  try {
    const result = await getCategoryList();
    if (result && result.code === 200) {
      categoryList.value = result.data || [];
    } else {
      showFailToast(result.msg || '加载失败');
    }
  } catch (error) {
    console.error('加载分类列表失败:', error);
    showFailToast('加载失败');
  }
};

const handleEdit = (item) => {
  editingCategory.value = item;
  formData.value = {
    name: item.name,
    icon: item.icon || '',
    sortOrder: item.sortOrder || 0
  };
  showAddDialog.value = true;
};

const handleDelete = (id) => {
  showConfirmDialog({
    title: '确认删除',
    message: '删除分类后，该分类下的商品将变为未分类状态，确定要删除吗？',
    confirmButtonColor: '#ff6b6b'
  }).then(async () => {
    try {
      const result = await deleteCategory(id);
      if (result && result.code === 200) {
        showSuccessToast('删除成功');
        loadCategoryList();
      } else {
        showFailToast(result.msg || '删除失败');
      }
    } catch (error) {
      showFailToast('删除失败');
    }
  }).catch(() => {});
};

const handleSubmit = async () => {
  if (!formData.value.name) {
    showFailToast('请输入分类名称');
    return;
  }

  try {
    const data = {
      name: formData.value.name,
      icon: formData.value.icon || null,
      sortOrder: formData.value.sortOrder || 0
    };
    
    let result;
    if (editingCategory.value) {
      result = await updateCategory(editingCategory.value.id, data);
    } else {
      result = await addCategory(data);
    }
    
    if (result && result.code === 200) {
      showSuccessToast(editingCategory.value ? '更新成功' : '添加成功');
      closeDialog();
      loadCategoryList();
    } else {
      showFailToast(result.msg || '操作失败');
    }
  } catch (error) {
    showFailToast('操作失败');
  }
};

const closeDialog = () => {
  showAddDialog.value = false;
  editingCategory.value = null;
  formData.value = {
    name: '',
    icon: '',
    sortOrder: 0
  };
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

onMounted(() => {
  loadCategoryList();
});
</script>

<style scoped>
.category-management {
  max-width: 1200px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
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

.category-table-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.category-table {
  width: 100%;
  border-collapse: collapse;
}

.category-table thead {
  background: #f8f9fa;
}

.category-table th {
  padding: 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
}

.category-table td {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #636e72;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
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

.required {
  color: #ff6b6b;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus {
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
