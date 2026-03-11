<template>
  <div class="wallet-view">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <span class="nav-back" @click="router.back()">&#x2039;</span>
      <span class="nav-title">我的钱包</span>
      <span class="nav-placeholder"></span>
    </div>

    <!-- 余额卡片 -->
    <div class="balance-card">
      <div class="balance-label">钱包余额（元）</div>
      <div class="balance-amount">
        <span class="currency">¥</span>
        <span class="amount">{{ loading ? '···' : balance }}</span>
      </div>
      <div class="balance-update-time" v-if="updateTime">更新于 {{ updateTime }}</div>
      <div class="card-actions">
        <button class="btn-action btn-recharge" @click="handleRecharge">
          <span class="btn-icon">＋</span>充值<span class="dev-badge">开发中</span>
        </button>
        <button class="btn-action btn-withdraw" @click="handleWithdraw">
          <span class="btn-icon">↑</span>提现<span class="dev-badge">开发中</span>
        </button>
      </div>
    </div>

    <!-- 安全设置 -->
    <div class="section-card">
      <div class="section-title">安全设置</div>
      <div class="menu-item" @click="openPasswordDialog">
        <div class="menu-left">
          <div class="menu-icon pass-icon">🔐</div>
          <div class="menu-text">
            <div class="menu-name">{{ hasPassword ? '修改支付密码' : '设置支付密码' }}</div>
            <div class="menu-desc">{{ hasPassword ? '定期修改密码，保障账户安全' : '设置6位数字支付密码' }}</div>
          </div>
        </div>
        <span class="arrow">›</span>
      </div>
    </div>

    <!-- 账单记录入口 -->
    <div class="section-card">
      <div class="section-title">账单</div>
      <div class="menu-item" @click="openTransactions">
        <div class="menu-left">
          <div class="menu-icon bill-icon">📋</div>
          <div class="menu-text">
            <div class="menu-name">交易记录</div>
            <div class="menu-desc">查看收支明细</div>
          </div>
        </div>
        <span class="arrow">›</span>
      </div>
    </div>

    <!-- 分步密码弹窗 -->
    <van-popup
      v-model:show="showPasswordDialog"
      position="bottom"
      round
      :style="{ height: 'auto', paddingBottom: '32px' }"
      @closed="resetPasswordForm"
    >
      <div class="popup-container">
        <div class="popup-header">
          <span class="popup-cancel" @click="showPasswordDialog = false">取消</span>
          <span class="popup-title">{{ stepTitle }}</span>
          <span class="popup-placeholder"></span>
        </div>
        <div class="popup-body">
          <!-- 步骤指示点 -->
          <div class="step-dots">
            <span
              v-for="i in totalSteps" :key="i"
              class="step-dot"
              :class="{ active: i === currentStep, done: i < currentStep }"
            ></span>
          </div>

          <div class="step-hint">{{ stepHint }}</div>

          <!-- 当前步骤的 PIN 输入 -->
          <div class="pin-row">
            <input
              v-for="(_, i) in 6" :key="currentStep + '-' + i"
              :ref="el => { if (el) currentPinRefs[i] = el }"
              class="pin-cell" :class="{ filled: currentPinArr[i] }"
              type="password" inputmode="numeric" maxlength="1"
              :value="currentPinArr[i]"
              @input="handleStepPinInput($event, i)"
              @keydown="handleStepPinKeydown($event, i)"
            />
          </div>

          <div v-if="passwordError" class="error-tip">{{ passwordError }}</div>
        </div>
      </div>
    </van-popup>

    <!-- 交易记录弹窗 -->
    <van-popup v-model:show="showTransactions" position="bottom" round :style="{ height: '75vh' }">
      <div class="popup-container trans-popup">
        <div class="popup-header">
          <span class="popup-cancel" @click="showTransactions = false">关闭</span>
          <span class="popup-title">交易记录</span>
          <span class="popup-placeholder"></span>
        </div>
        <div class="trans-list" @scroll="onTransScroll">
          <div v-if="transLoading && transactions.length === 0" class="trans-empty">加载中...</div>
          <div v-else-if="transactions.length === 0" class="trans-empty">暂无交易记录</div>
          <div v-for="item in transactions" :key="item.id" class="trans-item">
            <div class="trans-left">
              <div class="trans-type-icon" :class="getTransTypeClass(item.type)">{{ getTransTypeIcon(item.type) }}</div>
              <div class="trans-info">
                <div class="trans-name">{{ getTransTypeName(item.type) }}</div>
                <div class="trans-time">{{ formatTime(item.createTime) }}</div>
              </div>
            </div>
            <div class="trans-amount" :class="item.type === 'DEDUCT' || item.type === 'WITHDRAW' ? 'minus' : 'plus'">
              {{ item.type === 'DEDUCT' || item.type === 'WITHDRAW' ? '-' : '+' }}&#165;{{ Number(item.amount).toFixed(2) }}
            </div>
          </div>
          <div v-if="transLoadingMore" class="trans-empty">加载更多...</div>
          <div v-if="!transHasMore && transactions.length > 0" class="trans-no-more">已加载全部记录</div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showSuccessToast } from 'vant';
import { getCurrentWalletInfo, hasPayPassword, setPayPassword, updatePayPassword, getWalletTransactions } from '@/api/wallet';

const router = useRouter();
const loading = ref(true);
const balance = ref('0.00');
const updateTime = ref('');

const loadWallet = async () => {
  loading.value = true;
  try {
    const res = await getCurrentWalletInfo();
    if (res && res.data) {
      balance.value = parseFloat(res.data.balance || 0).toFixed(2);
      if (res.data.updateTime) updateTime.value = formatTime(res.data.updateTime);
    }
  } catch (e) { console.error('加载钱包失败', e); } finally { loading.value = false; }
};

const hasPassword = ref(false);
const loadHasPassword = async () => {
  try {
    const res = await hasPayPassword();
    if (res && res.data !== undefined) hasPassword.value = !!res.data;
  } catch (e) { console.error('查询密码状态失败', e); }
};

const handleRecharge = () => showToast({ message: '充值功能开发中，敬请期待', duration: 2000 });
const handleWithdraw = () => showToast({ message: '提现功能开发中，敬请期待', duration: 2000 });

const showPasswordDialog = ref(false);
const passwordError = ref('');
const submitting = ref(false);

// 分步状态
// 设置密码：step 1=输入密码, step 2=确认密码
// 修改密码：step 1=原密码, step 2=新密码, step 3=确认密码
const currentStep = ref(1);
const totalSteps = computed(() => hasPassword.value ? 3 : 2);

// 各步骤已收集的密码
const oldPin = ref('');
const newPin = ref('');
const confirmPin = ref('');

// 当前步骤的 PIN 数组和输入框引用
const currentPinArr = ref(Array(6).fill(''));
const currentPinRefs = ref([]);

// 当前步骤标题和提示
const stepTitle = computed(() => hasPassword.value ? '修改支付密码' : '设置支付密码');
const stepHint = computed(() => {
  if (hasPassword.value) {
    return ['请输入原支付密码', '请设置新支付密码', '请再次输入新密码'][currentStep.value - 1];
  }
  return ['请设置支付密码（6位数字）', '请再次输入密码确认'][currentStep.value - 1];
});

const openPasswordDialog = () => {
  resetPasswordForm();
  showPasswordDialog.value = true;
  // 弹窗打开后自动聚焦第一格
  setTimeout(() => currentPinRefs.value[0]?.focus(), 300);
};

const resetPasswordForm = () => {
  currentStep.value = 1;
  oldPin.value = '';
  newPin.value = '';
  confirmPin.value = '';
  currentPinArr.value = Array(6).fill('');
  currentPinRefs.value = [];
  passwordError.value = '';
  submitting.value = false;
};

const handleStepPinInput = (e, index) => {
  const val = e.target.value.replace(/\D/g, '').slice(-1);
  const arr = [...currentPinArr.value];
  arr[index] = val;
  currentPinArr.value = arr;
  passwordError.value = '';
  if (val && index < 5) {
    currentPinRefs.value[index + 1]?.focus();
  }
  // 最后一格填完后自动进入下一步
  if (val && index === 5) {
    const pin = arr.join('');
    setTimeout(() => advanceStep(pin), 150);
  }
};

const handleStepPinKeydown = (e, index) => {
  if (e.key === 'Backspace') {
    const arr = [...currentPinArr.value];
    if (arr[index]) {
      arr[index] = '';
      currentPinArr.value = arr;
    } else if (index > 0) {
      arr[index - 1] = '';
      currentPinArr.value = arr;
      currentPinRefs.value[index - 1]?.focus();
    }
  }
};

const advanceStep = async (pin) => {
  passwordError.value = '';
  const step = currentStep.value;

  if (hasPassword.value) {
    // 修改密码：step1=原密码, step2=新密码, step3=确认
    if (step === 1) {
      oldPin.value = pin;
      goNextStep();
    } else if (step === 2) {
      newPin.value = pin;
      goNextStep();
    } else if (step === 3) {
      confirmPin.value = pin;
      if (pin !== newPin.value) {
        passwordError.value = '两次输入的密码不一致，请重新输入';
        currentPinArr.value = Array(6).fill('');
        setTimeout(() => currentPinRefs.value[0]?.focus(), 100);
        return;
      }
      await doSubmit();
    }
  } else {
    // 设置密码：step1=密码, step2=确认
    if (step === 1) {
      newPin.value = pin;
      goNextStep();
    } else if (step === 2) {
      confirmPin.value = pin;
      if (pin !== newPin.value) {
        passwordError.value = '两次输入的密码不一致，请重新输入';
        currentPinArr.value = Array(6).fill('');
        setTimeout(() => currentPinRefs.value[0]?.focus(), 100);
        return;
      }
      await doSubmit();
    }
  }
};

const goNextStep = () => {
  currentStep.value++;
  currentPinArr.value = Array(6).fill('');
  currentPinRefs.value = [];
  setTimeout(() => currentPinRefs.value[0]?.focus(), 100);
};

const doSubmit = async () => {
  if (submitting.value) return;
  submitting.value = true;
  try {
    if (hasPassword.value) {
      await updatePayPassword({ oldPassword: oldPin.value, newPassword: newPin.value });
      showSuccessToast('密码修改成功');
    } else {
      await setPayPassword({ password: newPin.value });
      showSuccessToast('密码设置成功');
      hasPassword.value = true;
    }
    showPasswordDialog.value = false;
  } catch (e) {
    passwordError.value = e?.response?.data?.msg || e?.message || '操作失败，请重试';
    currentPinArr.value = Array(6).fill('');
    setTimeout(() => currentPinRefs.value[0]?.focus(), 100);
  } finally {
    submitting.value = false;
  }
};

const showTransactions = ref(false);
const transactions = ref([]);
const transLoading = ref(false);
const transLoadingMore = ref(false);
const transPage = ref(1);
const transHasMore = ref(true);

const loadTransactions = async (reset = false) => {
  if (reset) { transPage.value = 1; transactions.value = []; transHasMore.value = true; }
  if (!transHasMore.value) return;
  if (transPage.value === 1) transLoading.value = true; else transLoadingMore.value = true;
  try {
    const res = await getWalletTransactions({ page: transPage.value, pageSize: 10 });
    if (res && res.data) {
      const list = res.data.list || [];
      transactions.value = [...transactions.value, ...list];
      transHasMore.value = transactions.value.length < (res.data.total || 0);
      if (transHasMore.value) transPage.value++;
    }
  } catch (e) { console.error('加载交易记录失败', e); } finally { transLoading.value = false; transLoadingMore.value = false; }
};

const openTransactions = () => { showTransactions.value = true; if (transactions.value.length === 0) loadTransactions(true); };
const onTransScroll = (e) => {
  const el = e.target;
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 50 && !transLoadingMore.value && transHasMore.value) loadTransactions();
};

const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const d = new Date(timeStr);
  if (isNaN(d.getTime())) return String(timeStr);
  const pad = (n) => String(n).padStart(2, '0');
  return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes());
};
const getTransTypeName = (type) => ({ RECHARGE: '充值', WITHDRAW: '提现', DEDUCT: '消费', ADD: '收入', REFUND: '退款' }[type] || type);
const getTransTypeIcon = (type) => ({ RECHARGE: '↓', WITHDRAW: '↑', DEDUCT: '-', ADD: '+', REFUND: '↩' }[type] || '·');
const getTransTypeClass = (type) => (type === 'DEDUCT' || type === 'WITHDRAW') ? 'icon-minus' : 'icon-plus';

onMounted(() => { loadWallet(); loadHasPassword(); });
</script>

<style scoped>
* { box-sizing: border-box; -webkit-tap-highlight-color: transparent; }

.wallet-view {
  min-height: 100vh;
  background: #f0f2f5;
  font-family: 'PingFang SC', 'Helvetica Neue', sans-serif;
  padding-bottom: 40px;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 52px 20px 20px;
  background: linear-gradient(145deg, #1c2340 0%, #2d3561 100%);
}
.nav-back { font-size: 36px; color: rgba(255,255,255,0.9); cursor: pointer; line-height: 1; width: 40px; font-weight: 300; }
.nav-title { font-size: 18px; font-weight: 600; color: #fff; letter-spacing: 1px; }
.nav-placeholder { width: 40px; }

.balance-card {
  background: linear-gradient(145deg, #1c2340 0%, #2d3561 50%, #1a2a4a 100%);
  padding: 0 28px 36px;
  position: relative;
  overflow: hidden;
}
.balance-card::before {
  content: ''; position: absolute; top: -40px; right: -40px; width: 200px; height: 200px;
  background: radial-gradient(circle, rgba(99,179,237,0.12) 0%, transparent 70%);
  border-radius: 50%; pointer-events: none;
}
.balance-card::after {
  content: ''; position: absolute; bottom: -60px; left: -40px; width: 180px; height: 180px;
  background: radial-gradient(circle, rgba(167,139,250,0.1) 0%, transparent 70%);
  border-radius: 50%; pointer-events: none;
}
.balance-label { font-size: 13px; color: rgba(255,255,255,0.6); letter-spacing: 0.5px; margin-bottom: 10px; }
.balance-amount { display: flex; align-items: baseline; gap: 4px; margin-bottom: 6px; position: relative; z-index: 1; }
.currency { font-size: 22px; font-weight: 500; color: rgba(255,255,255,0.8); }
.amount { font-size: 52px; font-weight: 700; color: #fff; letter-spacing: -1px; line-height: 1; }
.balance-update-time { font-size: 12px; color: rgba(255,255,255,0.4); margin-bottom: 28px; }

.card-actions { display: flex; gap: 12px; position: relative; z-index: 1; }
.btn-action {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 14px 0; border: none; border-radius: 12px; font-size: 15px; font-weight: 600;
  cursor: pointer; position: relative; transition: opacity 0.15s; letter-spacing: 1px;
}
.btn-action:active { opacity: 0.75; transform: scale(0.98); }
.btn-recharge { background: rgba(255,255,255,0.18); color: #fff; backdrop-filter: blur(4px); border: 1px solid rgba(255,255,255,0.2); }
.btn-withdraw { background: rgba(255,255,255,0.07); color: rgba(255,255,255,0.65); border: 1px solid rgba(255,255,255,0.12); }
.btn-icon { font-size: 16px; }
.dev-badge {
  position: absolute; top: -8px; right: 6px;
  background: #f59e0b; color: #fff;
  font-size: 10px; padding: 1px 6px; border-radius: 8px; font-weight: 500; letter-spacing: 0;
}

.section-card {
  margin: 12px 16px 0;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 8px rgba(0,0,0,0.06);
}
.section-title {
  padding: 16px 20px 8px;
  font-size: 13px;
  color: #999;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.menu-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.15s;
  border-top: 1px solid #f5f5f5;
}
.menu-item:active { background: #f8f9fa; }
.menu-left { display: flex; align-items: center; gap: 14px; }
.menu-icon { font-size: 22px; width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 10px; }
.pass-icon { background: #fef3e2; }
.bill-icon { background: #e8f4fd; }
.menu-name { font-size: 15px; color: #333; font-weight: 500; margin-bottom: 2px; }
.menu-desc { font-size: 12px; color: #aaa; }
.arrow { font-size: 22px; color: #ccc; font-weight: 300; }

.popup-container { padding: 0; }
.popup-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 20px 12px;
  border-bottom: 1px solid #f0f0f0;
}
.popup-title { font-size: 16px; font-weight: 600; color: #333; }
.popup-cancel { font-size: 15px; color: #999; cursor: pointer; }
.popup-confirm { font-size: 15px; color: #4f46e5; font-weight: 600; cursor: pointer; }
.popup-confirm.disabled { color: #ccc; cursor: not-allowed; }
.popup-placeholder { width: 40px; }
.popup-body { padding: 28px 20px 16px; }

/* 步骤指示点 */
.step-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
}
.step-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e5e7eb;
  transition: all 0.3s;
}
.step-dot.active {
  background: #4f46e5;
  width: 24px;
  border-radius: 4px;
}
.step-dot.done {
  background: #a5b4fc;
}

/* 步骤提示文字 */
.step-hint {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-bottom: 28px;
  font-weight: 400;
}

.form-item { margin-bottom: 24px; }
.form-label { display: block; font-size: 14px; color: #666; margin-bottom: 12px; font-weight: 500; }
.pin-row { display: flex; gap: 10px; justify-content: center; }
.pin-cell {
  width: 44px; height: 52px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  text-align: center;
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  background: #f9fafb;
  outline: none;
  transition: border-color 0.2s, background 0.2s;
  -webkit-text-security: disc;
}
.pin-cell:focus { border-color: #4f46e5; background: #fff; box-shadow: 0 0 0 3px rgba(79,70,229,0.1); }
.pin-cell.filled { border-color: #4f46e5; background: #fff; }
.error-tip { color: #ef4444; font-size: 13px; text-align: center; margin-top: 4px; padding: 8px 0; }

.trans-popup { display: flex; flex-direction: column; }
.trans-list { flex: 1; overflow-y: auto; padding: 0 16px 16px; }
.trans-empty { text-align: center; color: #bbb; padding: 40px 0; font-size: 14px; }
.trans-no-more { text-align: center; color: #ddd; padding: 16px 0; font-size: 12px; }
.trans-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}
.trans-item:last-child { border-bottom: none; }
.trans-left { display: flex; align-items: center; gap: 12px; }
.trans-type-icon {
  width: 40px; height: 40px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; font-weight: 700;
}
.icon-plus { background: #dcfce7; color: #16a34a; }
.icon-minus { background: #fee2e2; color: #dc2626; }
.trans-name { font-size: 15px; color: #333; font-weight: 500; margin-bottom: 3px; }
.trans-time { font-size: 12px; color: #aaa; }
.trans-amount { font-size: 16px; font-weight: 700; }
.trans-amount.plus { color: #16a34a; }
.trans-amount.minus { color: #dc2626; }
</style>
